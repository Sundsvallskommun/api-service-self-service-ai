package se.sundsvall.selfserviceai.integration.db;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class SessionRepositoryTest {
	private static final String MUNICIPALITY = "2281";

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private FileRepository fileRepository;

	@Test
	void createSession() {
		final var sessionId = UUID.randomUUID().toString();
		final var session = SessionEntity.builder()
			.withMunicipalityId(MUNICIPALITY)
			.withSessionId(sessionId)
			.build();

		assertThat(sessionRepository.existsById(sessionId)).isFalse();
		sessionRepository.save(session);
		assertThat(sessionRepository.existsById(sessionId)).isTrue();
	}

	@Test
	void existsBySessionIdAndMunicipalityId() {
		final var sessionId = "4dc21d5e-8a70-45fb-b225-367fcd383a2e";

		assertThat(sessionRepository.existsBySessionIdAndMunicipalityId(sessionId, MUNICIPALITY)).isTrue();
		assertThat(sessionRepository.existsBySessionIdAndMunicipalityId(UUID.randomUUID().toString(), MUNICIPALITY)).isFalse();
	}

	@Test
	void existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull() {
		final var sessionNotYetInitialized = "a6602aba-0b21-4abf-a869-60c583570129";
		final var sessionInitialized = "4dc21d5e-8a70-45fb-b225-367fcd383a2e";

		assertThat(sessionRepository.existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(sessionNotYetInitialized, MUNICIPALITY)).isFalse();
		assertThat(sessionRepository.existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(sessionInitialized, MUNICIPALITY)).isTrue();
	}

	@Test
	void getReferenceBySessionIdAndMunicipalityId() {
		final var sessionId = "4dc21d5e-8a70-45fb-b225-367fcd383a2e";
		final var session = sessionRepository.getReferenceBySessionIdAndMunicipalityId(sessionId, MUNICIPALITY);

		assertThat(session.getCreated()).isEqualTo(OffsetDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 0), OffsetDateTime.now(systemDefault()).getOffset()));
		assertThat(session.getInitialized()).isEqualTo(OffsetDateTime.of(LocalDateTime.of(2025, 1, 1, 11, 0, 30), OffsetDateTime.now(systemDefault()).getOffset()));
		assertThat(session.getInitiationStatus()).isEqualTo("Successfully initialized");
		assertThat(session.getLastAccessed()).isNull();
		assertThat(session.getMunicipalityId()).isEqualTo(MUNICIPALITY);
		assertThat(session.getFiles()).hasSize(2)
			.extracting(FileEntity::getFileId)
			.containsExactlyInAnyOrder("5ef193cd-96a7-4861-a33d-e01528618f2e", "2f60ca4c-828b-4f4e-818f-432d53d61f83");
	}

	@Test
	void findBySessionIdAndMunicipalityId() {
		final var sessionId = "4dc21d5e-8a70-45fb-b225-367fcd383a2e";

		assertThat(sessionRepository.findBySessionIdAndMunicipalityId(sessionId, MUNICIPALITY)).isPresent();
		assertThat(sessionRepository.findBySessionIdAndMunicipalityId(UUID.randomUUID().toString(), MUNICIPALITY)).isEmpty();
	}

	@Test
	@Sql(scripts = {
		"/db/scripts/truncate.sql",
		"/db/scripts/testdata-junit.sql"
	})
	void findAllByMunicipalityIdAndInitializedIsNull() {
		final var matches = sessionRepository.findAllByMunicipalityIdAndInitializedIsNull(MUNICIPALITY);

		assertThat(matches).hasSize(2)
			.extracting(SessionEntity::getSessionId)
			.containsExactlyInAnyOrder("8212c515-6f7a-4e1c-a6b4-a2e265f018ed", "a6602aba-0b21-4abf-a869-60c583570129");
	}

	@ParameterizedTest
	@MethodSource("danglingSessionProvider")
	@Sql(scripts = {
		"/db/scripts/truncate.sql",
		"/db/scripts/testdata-junit.sql"
	})
	void findAllByMunicipalityIdAndLastAccessedBeforeOrLastAccessedIsNull(OffsetDateTime timestamp, List<String> matchingSessionIds) {
		final var matches = sessionRepository.findAllByMunicipalityIdAndLastAccessedBeforeOrLastAccessedIsNull(MUNICIPALITY, timestamp);

		assertThat(matches).hasSize(matchingSessionIds.size())
			.extracting(SessionEntity::getSessionId)
			.containsExactlyInAnyOrderElementsOf(matchingSessionIds);
	}

	private static Stream<Arguments> danglingSessionProvider() {
		return Stream.of(
			// When sending todays date, all sessions should be returned
			Arguments.of(OffsetDateTime.now(), List.of(
				"a6602aba-0b21-4abf-a869-60c583570129",
				"4dc21d5e-8a70-45fb-b225-367fcd383a2e",
				"b72af369-1764-486d-8d4d-17158318f6dd",
				"158cfabe-1c3d-433c-b71f-1c909beaa291",
				"8212c515-6f7a-4e1c-a6b4-a2e265f018ed")),
			// When sending 2025-01-01 12:01:00, only sessions with no value in access date should be returned
			Arguments.of(OffsetDateTime.of(LocalDateTime.of(2025, 1, 1, 12, 1, 0), OffsetDateTime.now(systemDefault()).getOffset()), List.of(
				"a6602aba-0b21-4abf-a869-60c583570129",
				"4dc21d5e-8a70-45fb-b225-367fcd383a2e",
				"8212c515-6f7a-4e1c-a6b4-a2e265f018ed")),
			// When sending 2025-01-01 12:01:01, sessions with no value and the one with value '2025-01-01 12:01:00' in access date
			// should be returned
			Arguments.of(OffsetDateTime.of(LocalDateTime.of(2025, 1, 1, 12, 1, 1), OffsetDateTime.now(systemDefault()).getOffset()), List.of(
				"a6602aba-0b21-4abf-a869-60c583570129",
				"4dc21d5e-8a70-45fb-b225-367fcd383a2e",
				"158cfabe-1c3d-433c-b71f-1c909beaa291",
				"8212c515-6f7a-4e1c-a6b4-a2e265f018ed")));
	}

	@Test
	void updateSession() {
		final var sessionId = "a6602aba-0b21-4abf-a869-60c583570129";
		final var session = sessionRepository.getReferenceById(sessionId);
		final var file = fileRepository.save(FileEntity.builder().withFileId(UUID.randomUUID().toString()).build());

		assertThat(session.getInitialized()).isNull();
		assertThat(session.getInitiationStatus()).isNull();
		assertThat(session.getFiles()).isEmpty();

		session.setInitialized(OffsetDateTime.now());
		session.setInitiationStatus("Successfully initialized");
		session.getFiles().add(file);

		sessionRepository.save(session);

		final var updatedSession = sessionRepository.getReferenceById(sessionId);
		assertThat(updatedSession).usingRecursiveAssertion().isEqualTo(session);
	}

	@Test
	void deleteOfSessionAndFilesSuccessful() {
		final var sessionId = "b72af369-1764-486d-8d4d-17158318f6dd";
		final var session = sessionRepository.getReferenceById(sessionId);

		// To be able to delete session, no files can be attached to entity and must be deleted in database
		session.getFiles().forEach(file -> fileRepository.deleteById(file.getFileId()));
		session.getFiles().clear();

		sessionRepository.delete(session);
		assertThat(sessionRepository.existsById(sessionId)).isFalse();
	}

	@Test
	void deleteOfSessionAndFilesFails() { // Test to verify that cascading delete is not turned on
		final var sessionId = "158cfabe-1c3d-433c-b71f-1c909beaa291";
		final var fileId = "811bcd0e-fe12-448e-85c5-2248a4a12e6d";

		assertThat(fileRepository.existsById(fileId)).isTrue();

		assertThrows(DataIntegrityViolationException.class, () -> { // NOSONAR - we need to flush to trigger deletion as test is executed in a transactional environment
			sessionRepository.deleteBySessionIdAndMunicipalityId(sessionId, MUNICIPALITY);
			sessionRepository.flush();
		});

		assertThat(fileRepository.existsById(fileId)).isTrue();
	}

	@Test
	void deleteFailedSession() {
		final var sessionId = "8212c515-6f7a-4e1c-a6b4-a2e265f018ed";

		assertThat(sessionRepository.existsById(sessionId)).isTrue();
		sessionRepository.deleteBySessionIdAndMunicipalityId(sessionId, MUNICIPALITY);
		assertThat(sessionRepository.existsById(sessionId)).isFalse();
	}
}
