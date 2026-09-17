package se.sundsvall.selfserviceai.service;

import generated.se.sundsvall.eneo.AskResponse;
import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.measurementdata.Data;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.requestid.RequestId;
import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.api.model.SessionRequest;
import se.sundsvall.selfserviceai.api.model.SessionResponse;
import se.sundsvall.selfserviceai.api.model.SessionStatusResponse;
import se.sundsvall.selfserviceai.integration.agreement.AgreementIntegration;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;
import se.sundsvall.selfserviceai.integration.eneo.EneoIntegration;
import se.sundsvall.selfserviceai.integration.eneo.configuration.EneoProperties;
import se.sundsvall.selfserviceai.integration.eneo.mapper.AgreementDecorator;
import se.sundsvall.selfserviceai.integration.eneo.mapper.EneoMapper;
import se.sundsvall.selfserviceai.integration.eneo.mapper.InvoiceDecorator;
import se.sundsvall.selfserviceai.integration.eneo.mapper.MeasurementDecorator;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.EneoModel;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.installedbase.InstalledbaseIntegration;
import se.sundsvall.selfserviceai.integration.invoices.InvoicesIntegration;
import se.sundsvall.selfserviceai.integration.lime.LimeIntegration;
import se.sundsvall.selfserviceai.integration.measurementdata.MeasurementDataIntegration;

import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;
import static se.sundsvall.selfserviceai.api.model.SessionStatus.FAILED;
import static se.sundsvall.selfserviceai.api.model.SessionStatus.PENDING;
import static se.sundsvall.selfserviceai.api.model.SessionStatus.READY;
import static se.sundsvall.selfserviceai.integration.db.mapper.DatabaseMapper.toSessionEntity;
import static se.sundsvall.selfserviceai.integration.measurementdata.MeasurementDataIntegration.CATEGORIES;
import static se.sundsvall.selfserviceai.service.mapper.AssistantMapper.toQuestionResponse;
import static se.sundsvall.selfserviceai.service.mapper.AssistantMapper.toSessionResponse;
import static se.sundsvall.selfserviceai.service.util.StringUtils.sanitizeAndCompress;

@Service
public class AssistantService {

	private static final Logger LOG = LoggerFactory.getLogger(AssistantService.class);
	private static final String ERROR_SESSION_NOT_FOUND = "Session with id '%s' could not be found";
	private static final String STATUS_SUCCESS = "Successfully initialized";
	private static final String STATUS_PARTIAL = "Initialized without data from: %s";
	private static final String STATUS_FAILED = "Initialization failed. Error message is '%s'. Filter logs on log id '%s' for more information.";

	/**
	 * The outcome of fetching information from one source during the population of a session.
	 *
	 * @param source  name of the source, e.g. "invoices" or "measurementdata/ELECTRICITY"
	 * @param success true if the source answered, false if the fetch failed
	 * @param count   number of items the source returned
	 * @param millis  time the fetch took
	 */
	record SourceOutcome(String source, boolean success, int count, long millis) {
		@Override
		public String toString() {
			return "%s=%s (%d ms)".formatted(source, success ? String.valueOf(count) : "FAILED", millis);
		}
	}

	private final AgreementIntegration agreementIntegration;
	private final InstalledbaseIntegration installedbaseIntegration;
	private final EneoIntegration eneoIntegration;
	private final EneoMapper eneoMapper;
	private final EneoProperties eneoProperties;
	private final InvoicesIntegration invoicesIntegration;
	private final LimeIntegration limeIntegration;
	private final MeasurementDataIntegration measurementDataIntegration;
	private final SessionPersistenceService sessionPersistenceService;
	private final SessionRepository sessionRepository;

	public AssistantService(
		final AgreementIntegration agreementIntegration,
		final InstalledbaseIntegration installedbaseIntegration,
		final EneoIntegration eneoIntegration,
		final EneoMapper eneoMapper,
		final EneoProperties eneoProperties,
		final InvoicesIntegration invoicesIntegration,
		final LimeIntegration limeIntegration,
		final MeasurementDataIntegration measurementDataIntegration,
		final SessionPersistenceService sessionPersistenceService,
		final SessionRepository sessionRepository) {

		this.agreementIntegration = agreementIntegration;
		this.installedbaseIntegration = installedbaseIntegration;
		this.eneoIntegration = eneoIntegration;
		this.eneoMapper = eneoMapper;
		this.eneoProperties = eneoProperties;
		this.invoicesIntegration = invoicesIntegration;
		this.limeIntegration = limeIntegration;
		this.measurementDataIntegration = measurementDataIntegration;
		this.sessionPersistenceService = sessionPersistenceService;
		this.sessionRepository = sessionRepository;
	}

	private static long millisSince(final long startNanos) {
		return NANOSECONDS.toMillis(System.nanoTime() - startNanos);
	}

	/**
	 * Fetches information from one of the sources that enrich the installed base. A failure is logged and recorded in
	 * the outcome instead of being thrown, so that a failure in one source does not prevent enrichment from the others.
	 * The recorded outcome is what makes the missing data visible in the session status, instead of silently leaving the
	 * assistant with less information than the customer expects.
	 */
	private static <T> List<T> fetch(final List<SourceOutcome> outcomes, final String source, final Supplier<List<T>> supplier) {
		final var start = System.nanoTime();
		try {
			final var result = ofNullable(supplier.get()).orElse(emptyList());
			outcomes.add(new SourceOutcome(source, true, result.size(), millisSince(start)));
			return result;
		} catch (final Exception e) {
			outcomes.add(new SourceOutcome(source, false, 0, millisSince(start)));
			LOG.warn("Could not fetch information from '{}' after {} ms: {}", source, millisSince(start), sanitizeForLogging(e.getMessage()));
			return emptyList();
		}
	}

	private static String toStatus(final List<SourceOutcome> outcomes) {
		final var failedSources = outcomes.stream()
			.filter(outcome -> !outcome.success())
			.map(SourceOutcome::source)
			.toList();

		return failedSources.isEmpty() ? STATUS_SUCCESS : STATUS_PARTIAL.formatted(String.join(", ", failedSources));
	}

	/**
	 * Creates a session locally. The session in Eneo is started by the first question (see {@link #askQuestion}), as
	 * starting it here would require an initial question that nobody reads but that costs a full assistant answer, i.e.
	 * some ten seconds that the customer would wait for before the population of the session even begins.
	 *
	 * @param  municipalityId id of the municipality that owns the session
	 * @param  partyId        party id of the customer that the session is created for
	 * @return                the created session
	 */
	public SessionResponse createSession(final String municipalityId, final String partyId) {
		final var sessionId = UUID.randomUUID();
		sessionRepository.save(toSessionEntity(municipalityId, sessionId, partyId));

		return toSessionResponse(eneoProperties.assistantId(), sessionId);
	}

	@Async
	public void populateWithInformation(final UUID sessionId, final SessionRequest sessionRequest, final String requestId) {
		try {
			RequestId.init(ofNullable(requestId).filter(id -> !id.isBlank()).orElse(UUID.randomUUID().toString()));

			populate(sessionId, sessionRequest);
		} finally {
			RequestId.reset();
		}
	}

	private void populate(final UUID sessionId, final SessionRequest sessionRequest) {
		final var municipalityId = sessionRepository.findById(sessionId.toString())
			.map(SessionEntity::getMunicipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId)));

		final var partyId = sessionRequest.getPartyId();
		final var outcomes = new ArrayList<SourceOutcome>();
		final var start = System.nanoTime();

		try {
			// The installed base is not wrapped: a failure here fails the session, as an installed base that could not be
			// fetched must not be mistaken for a customer without installed base
			final var installedBaseStart = System.nanoTime();
			final var installedBases = installedbaseIntegration.getInstalledbases(municipalityId, partyId, sessionRequest.getCustomerEngagementOrgIds());
			outcomes.add(new SourceOutcome("installedbase", true, ofNullable(installedBases).map(Map::size).orElse(0), millisSince(installedBaseStart)));

			if (isNotEmpty(installedBases)) {
				uploadAndAttachFile(sessionId, municipalityId, sessionRequest, installedBases, outcomes);
			} else {
				final var sanitizedPartyId = sanitizeAndCompress(partyId);
				LOG.warn("No installed base information found for customer '{}' and counterparts {}", sanitizedPartyId, sanitizeAndCompress(sessionRequest.getCustomerEngagementOrgIds()));
				sessionPersistenceService.completeInitialization(sessionId.toString(),
					"No installed base information found for customer '%s' and counterparts %s".formatted(partyId, sessionRequest.getCustomerEngagementOrgIds()));
			}
			LOG.info("Session '{}' populated in {} ms: {}", sessionId, millisSince(start), outcomes);
		} catch (final Exception e) {
			LOG.error("Exception thrown when populating session '{}' after {} ms: {}", sessionId, millisSince(start), outcomes, e);
			// Update with failed information
			sessionPersistenceService.completeInitialization(sessionId.toString(), STATUS_FAILED.formatted(e.getMessage(), RequestId.get()));
		}
	}

	/**
	 * Builds the file content, stores it in Eneo and connects the stored file to the session.
	 *
	 * A file that never gets connected to a session must be removed from Eneo again, as nothing else will ever remove it.
	 * That is the case both when the session has been removed while it was being populated, and when the connection itself
	 * fails.
	 *
	 * @param sessionId      id of the session to populate
	 * @param municipalityId id of the municipality that owns the session
	 * @param sessionRequest the request that initiated the population
	 * @param installedBases the installed base information to build the file content from
	 * @param outcomes       the outcome of every source that has been fetched so far, extended by this method
	 */
	private void uploadAndAttachFile(final UUID sessionId, final String municipalityId, final SessionRequest sessionRequest, final Map<String, InstalledBaseCustomer> installedBases, final List<SourceOutcome> outcomes) {
		final var eneoModel = buildEneoModel(municipalityId, sessionRequest.getPartyId(), installedBases, outcomes);
		final var uploadStart = System.nanoTime();
		final var fileId = eneoIntegration.uploadFile(eneoModel);
		outcomes.add(new SourceOutcome("eneo-upload", true, 1, millisSince(uploadStart)));
		var isAttached = false;

		try {
			isAttached = sessionPersistenceService.attachFile(sessionId.toString(), fileId, eneoModel.getCustomerNumber(), toStatus(outcomes));

			if (!isAttached) {
				LOG.info("Session '{}' was removed while it was being populated with customer information", sessionId);
			}
		} finally {
			if (!isAttached) {
				eneoIntegration.deleteFile(fileId.toString());
			}
		}
	}

	private EneoModel buildEneoModel(final String municipalityId, final String partyId, final Map<String, InstalledBaseCustomer> installedBases, final List<SourceOutcome> outcomes) {
		final var eneoModel = eneoMapper.toEneoModel(installedBases);

		// Enrich all facility with agreement, invoice and measurement information. Each integration call is fetched
		// individually so a failure in one source does not prevent enrichment from the others, and every outcome is
		// recorded so that a missing source ends up in the session status
		final var facilities = ofNullable(eneoModel.getFacilities()).orElse(emptyList());

		final var invoices = fetch(outcomes, "invoices", () -> invoicesIntegration.getInvoices(municipalityId, partyId));
		final var decoratedInvoices = invoices.stream()
			.map(InvoiceDecorator::toDecoratedInvoice)
			.toList();

		AgreementDecorator.addAgreements(facilities, fetch(outcomes, "agreements", () -> agreementIntegration.getAgreements(municipalityId, partyId)));
		InvoiceDecorator.addInvoices(facilities, decoratedInvoices);
		MeasurementDecorator.addMeasurements(facilities, fetchMeasurements(municipalityId, partyId, facilities, outcomes));

		return eneoModel;
	}

	private List<Data> fetchMeasurements(final String municipalityId, final String partyId, final List<Facility> facilities, final List<SourceOutcome> outcomes) {
		return CATEGORIES.stream()
			.flatMap(category -> fetch(outcomes, "measurementdata/" + category, () -> ofNullable(measurementDataIntegration.getMeasurementData(municipalityId, partyId, facilities, category))
				.map(List::of)
				.orElse(emptyList())).stream())
			.toList();
	}

	public SessionStatusResponse isSessionReady(final String municipalityId, final UUID sessionId) {
		final var session = sessionRepository.findBySessionIdAndMunicipalityId(sessionId.toString(), municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId)));

		if (isNull(session.getInitialized())) {
			return SessionStatusResponse.builder()
				.withStatus(PENDING.name())
				.withDetail("Session is being initialized")
				.build();
		}
		if (isInitializationFailed(session)) {
			return SessionStatusResponse.builder()
				.withStatus(FAILED.name())
				.withDetail(session.getStatus())
				.build();
		}
		return SessionStatusResponse.builder()
			.withStatus(READY.name())
			.withDetail(STATUS_SUCCESS.equals(session.getStatus()) ? null : session.getStatus()) // Tells the frontend when the assistant is ready but has less data than expected
			.withEneoSessionId(session.getEneoSessionId()) // Lets the frontend talk to Eneo directly once the first question has started the session there
			.build();
	}

	private static boolean isInitializationFailed(final SessionEntity session) {
		return ofNullable(session.getStatus()).map(status -> status.startsWith("Initialization failed")).orElse(false);
	}

	public QuestionResponse askQuestion(final String municipalityId, final UUID sessionId, final String question) {
		final var session = sessionRepository.findBySessionIdAndMunicipalityId(sessionId.toString(), municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId)));

		if (isNull(session.getInitialized())) {
			return toQuestionResponse("Assistant is not ready yet");
		}
		if (isInitializationFailed(session)) {
			return toQuestionResponse("Assistant initialization failed, please create a new session");
		}

		final var fileIds = session.getFiles().stream().map(FileEntity::getFileId).toList();
		final var eneoResponse = isNull(session.getEneoSessionId())
			? startEneoSession(session, question, fileIds)
			: eneoIntegration.askFollowUp(eneoProperties.assistantId(), session.getEneoSessionId(), question, fileIds);

		if (eneoResponse.isPresent()) {
			session.setLastAccessed(OffsetDateTime.now(systemDefault()));
			sessionRepository.save(session);
		}

		return eneoResponse
			.map(askResponse -> toQuestionResponse(session.getSessionId(), askResponse))
			.orElse(null);
	}

	/**
	 * Asks the first question of a session, which starts the session in Eneo, and connects the started Eneo session to
	 * the session. The first writer wins if two first questions race, and the Eneo session that lost is removed again, as
	 * nothing else would ever remove it.
	 *
	 * @param  session  the session to start in Eneo
	 * @param  question the first question
	 * @param  fileIds  ids of the files that the assistant shall base its answer on
	 * @return          the response from the assistant, or empty if the assistant could not be reached
	 */
	private Optional<AskResponse> startEneoSession(final SessionEntity session, final String question, final List<String> fileIds) {
		final var eneoResponse = eneoIntegration.askAssistant(eneoProperties.assistantId(), question, fileIds);

		eneoResponse.map(AskResponse::getSessionId).map(UUID::toString).ifPresent(eneoSessionId -> {
			final var attachedEneoSessionId = sessionPersistenceService.attachEneoSession(session.getSessionId(), eneoSessionId);

			if (attachedEneoSessionId.filter(eneoSessionId::equals).isEmpty()) {
				LOG.info("Eneo session '{}' is not the one connected to session '{}', removing it", eneoSessionId, session.getSessionId());
				eneoIntegration.deleteSession(eneoProperties.assistantId(), eneoSessionId);
			}
			attachedEneoSessionId.ifPresent(session::setEneoSessionId);
		});

		return eneoResponse;
	}

	@Async
	public void deleteSessionById(final String municipalityId, final UUID sessionId, final String requestId) {
		try {
			RequestId.init(ofNullable(requestId).filter(id -> !id.isBlank()).orElse(UUID.randomUUID().toString()));

			final var sessionEntity = sessionPersistenceService.loadSession(sessionId.toString(), municipalityId)
				.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId)));

			removeSession(sessionEntity);
		} finally {
			RequestId.reset();
		}
	}

	/**
	 * Removes all sessions that have been inactive since the defined threshold.
	 *
	 * Every session is removed on its own, so that a failure for one session does not prevent the removal of the others.
	 * Sessions that are left behind are reported by throwing at the end, as the scheduler health indicator is only updated
	 * when the scheduled method throws.
	 *
	 * @param inactivityThreshold number of minutes a session may be inactive before it is removed
	 */
	public void cleanUpInactiveSessions(final Integer inactivityThreshold) {
		final var timestamp = OffsetDateTime.now(systemDefault()).minusMinutes(inactivityThreshold);

		final var sessions = sessionPersistenceService.loadInactiveSessions(timestamp).stream()
			.filter(session -> isSubjectForRemoval(timestamp, session))
			.toList();

		final var failedSessionIds = sessions.stream()
			.filter(session -> !removeSession(session))
			.map(SessionEntity::getSessionId)
			.toList();

		if (!failedSessionIds.isEmpty()) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "%d of %d inactive sessions could not be removed: %s. Filter logs on log id '%s' for more information."
				.formatted(failedSessionIds.size(), sessions.size(), failedSessionIds, RequestId.get()));
		}
	}

	/**
	 * Removes a session, its chat history and its files.
	 *
	 * A failure is logged and swallowed, as a failure for one session must not prevent the removal of the others. The
	 * outcome is returned instead, so that the caller can decide what an unremoved session means.
	 *
	 * @param  sessionEntity session to remove
	 * @return               false if the session is left behind, whether because the chat history could not be saved or
	 *                       because the removal failed
	 */
	private boolean removeSession(final SessionEntity sessionEntity) {
		try {
			if (!saveChatHistory(sessionEntity)) {
				return false;
			}

			deleteSession(sessionEntity);
			return true;
		} catch (final Exception e) {
			LOG.error("Exception thrown when removing session '{}'", sessionEntity.getSessionId(), e);
			return false;
		}
	}

	/**
	 * To be subject for removal, the session must either have a last accessed timestamp, or a created timestamp that is
	 * before the defined threshold for inactivity
	 *
	 * @param  timestamp timestamp when a session is interpreted as inactive
	 * @param  session   session to evaluate
	 * @return           true if the session is inactive and subject for removal, false otherwise
	 */
	private boolean isSubjectForRemoval(final OffsetDateTime timestamp, final SessionEntity session) {
		return Objects.nonNull(session.getLastAccessed()) || session.getCreated().isBefore(timestamp);
	}

	/**
	 * Saves the chat history of a session before it is removed.
	 *
	 * @param  sessionEntity session to save the chat history for
	 * @return               false if the history could not be saved, in which case the session must not be removed
	 */
	private boolean saveChatHistory(final SessionEntity sessionEntity) {
		try {
			// Only save chat history if the session has been started in Eneo, i.e. at least one question has been asked
			if (nonNull(sessionEntity.getInitialized()) && nonNull(sessionEntity.getEneoSessionId())) {
				eneoIntegration.getSession(eneoProperties.assistantId(), sessionEntity.getEneoSessionId())
					.ifPresent(session -> limeIntegration.saveChatHistory(sessionEntity.getPartyId(), sessionEntity.getCustomerNbr(), session));
			}
			return true;
		} catch (final Exception e) {
			LOG.error("Exception thrown when saving chat history for session", e);
			// Update with failed information
			sessionPersistenceService.updateStatus(sessionEntity.getSessionId(),
				"Failed to save chat history. Error message is '%s'. Filter logs on log id '%s' for more information.".formatted(e.getMessage(), RequestId.get()));
			return false;
		}
	}

	/**
	 * Removes a session and its files, first in Eneo and thereafter in the database. The removals in Eneo are performed
	 * outside of any transaction, as a row in the file table always represents a file that still exists in Eneo. The
	 * session itself only exists in Eneo if a first question has been asked.
	 * <p>
	 * The session in Eneo is removed before its files, as Eneo refuses to remove a file that is still attached to a chat
	 * (409, Eneo error code 9044). Removing the files first would therefore never succeed, and the session would be
	 * retried forever. A session that is already gone in Eneo counts as removed, so a retry after a failed file removal
	 * gets past the session and on to the files.
	 *
	 * @param sessionEntity session to remove
	 */
	private void deleteSession(final SessionEntity sessionEntity) {
		final var sessionRemovedInEneo = isNull(sessionEntity.getEneoSessionId()) // A session that never got a first question has nothing to remove in Eneo
			|| eneoIntegration.deleteSession(eneoProperties.assistantId(), sessionEntity.getEneoSessionId());

		// Files are only attempted once the chat that holds them is gone, otherwise Eneo would refuse every one of them
		final var removedFileIds = sessionRemovedInEneo
			? sessionEntity.getFiles().stream()
				.map(FileEntity::getFileId)
				.filter(eneoIntegration::deleteFile)
				.toList()
			: List.<String>of();

		sessionPersistenceService.finalizeDeletion(sessionEntity.getSessionId(), removedFileIds, sessionRemovedInEneo);
	}
}
