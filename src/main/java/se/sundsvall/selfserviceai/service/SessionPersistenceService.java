package se.sundsvall.selfserviceai.service;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.requestid.RequestId;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;

import static java.time.ZoneId.systemDefault;
import static se.sundsvall.selfserviceai.integration.db.mapper.DatabaseMapper.toFileEntity;

/**
 * Owns the transactional database operations that are performed on a session.
 *
 * All communication with Eneo and Lime is performed outside of these transactions by {@link AssistantService}, so that
 * no transaction is held open while waiting for another system. Every method that writes locks the session row and
 * reads the current state before it acts, which serializes the initialization of a session against the removal of it.
 */
@Service
public class SessionPersistenceService {

	private static final Logger LOG = LoggerFactory.getLogger(SessionPersistenceService.class);

	private final FileRepository fileRepository;
	private final SessionRepository sessionRepository;

	public SessionPersistenceService(final FileRepository fileRepository, final SessionRepository sessionRepository) {
		this.fileRepository = fileRepository;
		this.sessionRepository = sessionRepository;
	}

	private static SessionEntity withInitializedFiles(final SessionEntity session) {
		Hibernate.initialize(session.getFiles()); // The files are read outside of this transaction
		return session;
	}

	/**
	 * Reads a session together with its files.
	 *
	 * @param  sessionId      id of the session to read
	 * @param  municipalityId id of the municipality that owns the session
	 * @return                the session, if it exists
	 */
	@Transactional(readOnly = true)
	public Optional<SessionEntity> loadSession(final String sessionId, final String municipalityId) {
		return sessionRepository.findBySessionIdAndMunicipalityId(sessionId, municipalityId)
			.map(SessionPersistenceService::withInitializedFiles);
	}

	/**
	 * Reads all sessions that have not been used since the sent in timestamp, together with their files.
	 *
	 * @param  timestamp timestamp when a session is interpreted as inactive
	 * @return           all inactive sessions, within all municipalities
	 */
	@Transactional(readOnly = true)
	public List<SessionEntity> loadInactiveSessions(final OffsetDateTime timestamp) {
		return sessionRepository.findAllByLastAccessedBeforeOrLastAccessedIsNull(timestamp).stream()
			.map(SessionPersistenceService::withInitializedFiles)
			.toList();
	}

	/**
	 * Connects a file that has been uploaded to Eneo with the session it was created for, and marks the session as
	 * initialized.
	 *
	 * @param  sessionId   id of the session to connect the file to
	 * @param  fileId      id of the file in Eneo
	 * @param  customerNbr customer number that the file content describes
	 * @param  status      status to set on the session
	 * @return             false if the session no longer exists, i.e. it has been removed while it was being populated
	 */
	@Transactional
	public boolean attachFile(final String sessionId, final UUID fileId, final String customerNbr, final String status) {
		final var session = sessionRepository.findForUpdateBySessionId(sessionId);

		session.ifPresent(existingSession -> attachFileTo(existingSession, fileId, customerNbr, status));

		return session.isPresent();
	}

	private void attachFileTo(final SessionEntity session, final UUID fileId, final String customerNbr, final String status) {
		session.getFiles().add(fileRepository.save(toFileEntity(fileId)));
		session.setCustomerNbr(customerNbr);
		markInitialized(session, status);
	}

	/**
	 * A session is initialized when the outcome of the population is known, whether information was found or not. The
	 * status explains the outcome and is therefore always set together with the timestamp.
	 *
	 * @param session session to mark as initialized
	 * @param status  status describing the outcome of the population
	 */
	private void markInitialized(final SessionEntity session, final String status) {
		session.setInitialized(OffsetDateTime.now(systemDefault()));
		session.setStatus(status);
	}

	/**
	 * Marks a session as initialized without connecting any file to it.
	 *
	 * @param sessionId id of the session to update
	 * @param status    status to set on the session
	 */
	@Transactional
	public void completeInitialization(final String sessionId, final String status) {
		sessionRepository.findForUpdateBySessionId(sessionId)
			.ifPresent(session -> markInitialized(session, status));
	}

	/**
	 * Updates the status of a session.
	 *
	 * @param sessionId id of the session to update
	 * @param status    status to set on the session
	 */
	@Transactional
	public void updateStatus(final String sessionId, final String status) {
		sessionRepository.findForUpdateBySessionId(sessionId)
			.ifPresent(session -> session.setStatus(status));
	}

	/**
	 * Removes the database traces of a session whose content has been removed in Eneo.
	 *
	 * The files of the session are read again within this transaction, as another transaction may have connected a file to
	 * the session since the removal in Eneo was started. Such a file is not part of the confirmed removals and is therefore
	 * left untouched, which also keeps the session itself alive.
	 *
	 * @param sessionId            id of the session to remove
	 * @param removedFileIds       ids of the files that have been confirmed removed in Eneo
	 * @param sessionRemovedInEneo signal if the session itself has been removed in Eneo
	 */
	@Transactional
	public void finalizeDeletion(final String sessionId, final Collection<String> removedFileIds, final boolean sessionRemovedInEneo) {
		sessionRepository.findForUpdateBySessionId(sessionId)
			.ifPresent(session -> removeTracesOf(session, removedFileIds, sessionRemovedInEneo));
	}

	/**
	 * Removes a session that has nothing left in Eneo. A session that has something left is instead marked as failed and
	 * retried by the scheduled clean up.
	 *
	 * @param session              session to remove
	 * @param removedFileIds       ids of the files that have been confirmed removed in Eneo
	 * @param sessionRemovedInEneo signal if the session itself has been removed in Eneo
	 */
	private void removeTracesOf(final SessionEntity session, final Collection<String> removedFileIds, final boolean sessionRemovedInEneo) {
		removeFilesRemovedInEneo(session, removedFileIds);

		if (session.getFiles().isEmpty() && sessionRemovedInEneo) {
			sessionRepository.delete(session);
		} else {
			LOG.error("Could not delete session: {}", session.getSessionId());
			session.setStatus("Failed to delete session, filter logs on log id '%s' for more information".formatted(RequestId.get()));
		}
	}

	/**
	 * Removes the rows of the files that have been confirmed removed in Eneo. A row that is left behind represents a file
	 * that still exists in Eneo, and it keeps the session alive until a later attempt manages to remove the file there.
	 *
	 * @param session        session to remove files from
	 * @param removedFileIds ids of the files that have been confirmed removed in Eneo
	 */
	private void removeFilesRemovedInEneo(final SessionEntity session, final Collection<String> removedFileIds) {
		final var filesRemovedInEneo = session.getFiles().stream()
			.filter(file -> removedFileIds.contains(file.getFileId()))
			.toList();

		fileRepository.deleteAll(filesRemovedInEneo);
		session.getFiles().removeAll(filesRemovedInEneo);
	}
}
