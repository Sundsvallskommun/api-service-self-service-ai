package se.sundsvall.selfserviceai.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

/**
 * Each method of {@link SessionPersistenceService} owns its own transaction, so the test method must run without one of
 * its own.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Import(SessionPersistenceService.class)
@Transactional(propagation = NOT_SUPPORTED)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class SessionPersistenceServiceTest {

	private static final String MUNICIPALITY_ID = "2281";

	// Created but not yet initialized, without files
	private static final String PENDING_SESSION_ID = "a6602aba-0b21-4abf-a869-60c583570129";

	// Failed in initialization, never started in Eneo
	private static final String FAILED_SESSION_ID = "8212c515-6f7a-4e1c-a6b4-a2e265f018ed";

	// Initialized and accessed, with two files
	private static final String INITIALIZED_SESSION_ID = "4dc21d5e-8a70-45fb-b225-367fcd383a2e";
	private static final List<String> INITIALIZED_SESSION_FILE_IDS = List.of("5ef193cd-96a7-4861-a33d-e01528618f2e", "2f60ca4c-828b-4f4e-818f-432d53d61f83");

	@Autowired
	private SessionPersistenceService sessionPersistenceService;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private FileRepository fileRepository;

	@Test
	void loadSessionReadsFilesWithinTheTransaction() {
		final var session = sessionPersistenceService.loadSession(INITIALIZED_SESSION_ID, MUNICIPALITY_ID).orElseThrow();

		// The files must be readable although the transaction has ended
		assertThat(session.getFiles()).extracting(FileEntity::getFileId).containsExactlyInAnyOrderElementsOf(INITIALIZED_SESSION_FILE_IDS);
	}

	@Test
	void loadSessionForOtherMunicipality() {
		assertThat(sessionPersistenceService.loadSession(INITIALIZED_SESSION_ID, "1984")).isEmpty();
	}

	@Test
	void loadInactiveSessionsReadsFilesWithinTheTransaction() {
		final var sessions = sessionPersistenceService.loadInactiveSessions(OffsetDateTime.now());

		assertThat(sessions).hasSize(5)
			.filteredOn(session -> INITIALIZED_SESSION_ID.equals(session.getSessionId()))
			.singleElement()
			.satisfies(session -> assertThat(session.getFiles()).extracting(FileEntity::getFileId).containsExactlyInAnyOrderElementsOf(INITIALIZED_SESSION_FILE_IDS));
	}

	@Test
	void attachFile() {
		final var fileId = UUID.randomUUID();

		final var result = sessionPersistenceService.attachFile(PENDING_SESSION_ID, fileId, "16324", "Successfully initialized");

		assertThat(result).isTrue();
		assertThat(fileRepository.existsById(fileId.toString())).isTrue();
		assertThat(sessionPersistenceService.loadSession(PENDING_SESSION_ID, MUNICIPALITY_ID)).hasValueSatisfying(session -> {
			assertThat(session.getFiles()).extracting(FileEntity::getFileId).containsExactly(fileId.toString());
			assertThat(session.getCustomerNbr()).isEqualTo("16324");
			assertThat(session.getInitialized()).isNotNull();
			assertThat(session.getStatus()).isEqualTo("Successfully initialized");
		});
	}

	@Test
	void attachFileToRemovedSession() {
		final var fileId = UUID.randomUUID();

		final var result = sessionPersistenceService.attachFile(UUID.randomUUID().toString(), fileId, "16324", "Successfully initialized");

		assertThat(result).isFalse();
		assertThat(fileRepository.existsById(fileId.toString())).isFalse();
	}

	@Test
	void attachEneoSession() {
		final var eneoSessionId = UUID.randomUUID().toString();

		final var result = sessionPersistenceService.attachEneoSession(FAILED_SESSION_ID, eneoSessionId);

		assertThat(result).hasValue(eneoSessionId);
		assertThat(sessionRepository.findById(FAILED_SESSION_ID)).hasValueSatisfying(session -> assertThat(session.getEneoSessionId()).isEqualTo(eneoSessionId));
	}

	@Test
	void attachEneoSessionWhenAlreadyAttached() {
		// The first writer wins, so the already connected Eneo session is kept and returned
		final var result = sessionPersistenceService.attachEneoSession(INITIALIZED_SESSION_ID, UUID.randomUUID().toString());

		assertThat(result).hasValue(INITIALIZED_SESSION_ID);
		assertThat(sessionRepository.findById(INITIALIZED_SESSION_ID)).hasValueSatisfying(session -> assertThat(session.getEneoSessionId()).isEqualTo(INITIALIZED_SESSION_ID));
	}

	@Test
	void attachEneoSessionWhenAnotherWriterWonAfterTheSessionWasLoaded() {
		// The entity is loaded into the persistence context first, as the request does before the first question is asked
		// (open-in-view), and another request then wins the race in its own transaction. The loser must see the winner's
		// Eneo session, not the null that is still on the entity in memory.
		final var winner = UUID.randomUUID().toString();
		final var loser = UUID.randomUUID().toString();

		final var result = new TransactionTemplate(transactionManager).execute(status -> {
			assertThat(sessionRepository.findById(FAILED_SESSION_ID)).hasValueSatisfying(session -> assertThat(session.getEneoSessionId()).isNull());

			final var otherRequest = new TransactionTemplate(transactionManager);
			otherRequest.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
			otherRequest.executeWithoutResult(otherStatus -> sessionPersistenceService.attachEneoSession(FAILED_SESSION_ID, winner));

			return sessionPersistenceService.attachEneoSession(FAILED_SESSION_ID, loser);
		});

		assertThat(result).hasValue(winner);
		assertThat(sessionRepository.findById(FAILED_SESSION_ID)).hasValueSatisfying(session -> assertThat(session.getEneoSessionId()).isEqualTo(winner));
	}

	@Test
	void attachEneoSessionToRemovedSession() {
		assertThat(sessionPersistenceService.attachEneoSession(UUID.randomUUID().toString(), UUID.randomUUID().toString())).isEmpty();
	}

	@Test
	void completeInitialization() {
		sessionPersistenceService.completeInitialization(PENDING_SESSION_ID, "No installed base information found");

		assertThat(sessionRepository.findById(PENDING_SESSION_ID)).hasValueSatisfying(session -> {
			assertThat(session.getInitialized()).isNotNull();
			assertThat(session.getStatus()).isEqualTo("No installed base information found");
		});
	}

	@Test
	void updateStatusLeavesInitializedUntouched() {
		sessionPersistenceService.updateStatus(PENDING_SESSION_ID, "Failed to save chat history");

		assertThat(sessionRepository.findById(PENDING_SESSION_ID)).hasValueSatisfying(session -> {
			assertThat(session.getInitialized()).isNull();
			assertThat(session.getStatus()).isEqualTo("Failed to save chat history");
		});
	}

	@Test
	void finalizeDeletionRemovesSessionAndItsFiles() {
		sessionPersistenceService.finalizeDeletion(INITIALIZED_SESSION_ID, INITIALIZED_SESSION_FILE_IDS, true);

		assertThat(sessionRepository.existsById(INITIALIZED_SESSION_ID)).isFalse();
		assertThat(fileRepository.findAllById(INITIALIZED_SESSION_FILE_IDS)).isEmpty();
	}

	@Test
	void finalizeDeletionKeepsSessionWhenSessionRemainsInEneo() {
		sessionPersistenceService.finalizeDeletion(INITIALIZED_SESSION_ID, INITIALIZED_SESSION_FILE_IDS, false);

		// The files are confirmed removed in Eneo and may be removed here, but the session must be kept and retried
		assertThat(fileRepository.findAllById(INITIALIZED_SESSION_FILE_IDS)).isEmpty();
		assertThat(sessionRepository.findById(INITIALIZED_SESSION_ID)).hasValueSatisfying(session -> assertThat(session.getStatus())
			.startsWith("Failed to delete session, filter logs on log id '"));
	}

	@Test
	void finalizeDeletionKeepsFilesThatRemainInEneo() {
		sessionPersistenceService.finalizeDeletion(INITIALIZED_SESSION_ID, emptyList(), true);

		// A row in the file table represents a file that still exists in Eneo and must not be removed here
		assertThat(fileRepository.findAllById(INITIALIZED_SESSION_FILE_IDS)).hasSize(2);
		assertThat(sessionRepository.findById(INITIALIZED_SESSION_ID)).hasValueSatisfying(session -> assertThat(session.getStatus())
			.startsWith("Failed to delete session, filter logs on log id '"));
	}

	@Test
	void finalizeDeletionForRemovedSession() {
		final var sessionId = UUID.randomUUID().toString();

		sessionPersistenceService.finalizeDeletion(sessionId, emptyList(), true);

		assertThat(sessionRepository.existsById(sessionId)).isFalse();
	}
}
