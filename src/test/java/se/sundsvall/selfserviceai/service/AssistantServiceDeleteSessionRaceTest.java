package se.sundsvall.selfserviceai.service;

import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.selfserviceai.integration.agreement.AgreementIntegration;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;
import se.sundsvall.selfserviceai.integration.eneo.EneoIntegration;
import se.sundsvall.selfserviceai.integration.eneo.configuration.EneoProperties;
import se.sundsvall.selfserviceai.integration.eneo.mapper.EneoMapper;
import se.sundsvall.selfserviceai.integration.installedbase.InstalledbaseIntegration;
import se.sundsvall.selfserviceai.integration.invoices.InvoicesIntegration;
import se.sundsvall.selfserviceai.integration.lime.LimeIntegration;
import se.sundsvall.selfserviceai.integration.measurementdata.MeasurementDataIntegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

/**
 * Verifies the race between the two asynchronous flows in {@link AssistantService}:
 *
 * <ol>
 * <li>{@code populateWithInformation} collects customer information, uploads the result to Eneo and connects the
 * resulting file to the session.</li>
 * <li>{@code deleteSessionById} reads the session, removes its content in Eneo and thereafter removes the database
 * traces of it.</li>
 * </ol>
 *
 * If the file row is committed after the session has been read for deletion, the deletion must not remove the session -
 * the file it points at still exists in Eneo, and a row in the file table always represents a file that must be removed
 * there. Before this was handled the deletion instead failed with a foreign key violation on {@code fk_session_file}.
 *
 * The test method runs without a transaction of its own, so that every call to {@link SessionPersistenceService} gets
 * its own transaction, exactly as in the application.
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
class AssistantServiceDeleteSessionRaceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ASSISTANT_ID = "assistant-id";

	// Session that is created but not yet initialized, i.e. populateWithInformation is still running for it
	private static final String SESSION_ID = "a6602aba-0b21-4abf-a869-60c583570129";

	@Autowired
	private SessionPersistenceService sessionPersistenceService;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private DataSource dataSource;

	private EneoIntegration eneoIntegrationMock;
	private AssistantService assistantService;

	@BeforeEach
	void setUp() {
		eneoIntegrationMock = mock(EneoIntegration.class);

		// The service is instantiated directly to bypass the @Async proxy, so that the removal runs synchronously
		assistantService = new AssistantService(
			mock(AgreementIntegration.class),
			mock(InstalledbaseIntegration.class),
			eneoIntegrationMock,
			mock(EneoMapper.class),
			new EneoProperties(30, 5, ASSISTANT_ID, "api-key"),
			mock(InvoicesIntegration.class),
			mock(LimeIntegration.class),
			mock(MeasurementDataIntegration.class),
			sessionPersistenceService,
			sessionRepository);
	}

	@Test
	void deleteSessionKeepsSessionWhenFileIsAttachedByConcurrentTransaction() {
		final var requestId = UUID.randomUUID().toString();
		final var concurrentFileId = UUID.randomUUID().toString();

		assertThat(sessionPersistenceService.loadSession(SESSION_ID, MUNICIPALITY_ID).orElseThrow().getFiles()).isEmpty();

		// populateWithInformation commits its file row while the removal in Eneo is ongoing, i.e. after the session has been
		// read for deletion but before its database traces are removed
		when(eneoIntegrationMock.deleteSession(ASSISTANT_ID, SESSION_ID)).thenAnswer(invocation -> {
			attachFileToSessionInSeparateTransaction(concurrentFileId);
			return true;
		});

		assistantService.deleteSessionById(MUNICIPALITY_ID, UUID.fromString(SESSION_ID), requestId);

		// The file was not part of the removal in Eneo, so neither it nor the session may be removed here
		assertThat(fileRepository.existsById(concurrentFileId)).isTrue();
		assertThat(sessionRepository.findById(SESSION_ID)).hasValueSatisfying(session -> assertThat(session.getStatus())
			.isEqualTo("Failed to delete session, filter logs on log id '%s' for more information".formatted(requestId)));

		verify(eneoIntegrationMock, never()).deleteFile(any());
	}

	@Test
	void deleteSessionRemovesSessionWhenNoConcurrentTransactionInterferes() {
		when(eneoIntegrationMock.deleteSession(ASSISTANT_ID, SESSION_ID)).thenReturn(true);

		assistantService.deleteSessionById(MUNICIPALITY_ID, UUID.fromString(SESSION_ID), UUID.randomUUID().toString());

		assertThat(sessionRepository.existsById(SESSION_ID)).isFalse();
	}

	/**
	 * Inserts and commits a file row pointing at the session, using a connection of its own so that the insert is
	 * performed by another transaction than the one used by the removal.
	 */
	private void attachFileToSessionInSeparateTransaction(final String fileId) throws SQLException {
		try (var connection = dataSource.getConnection();
			var statement = connection.prepareStatement("insert into file(file_id, session_id) values(?, ?)")) {
			connection.setAutoCommit(true);
			statement.setString(1, fileId);
			statement.setString(2, SESSION_ID);
			statement.executeUpdate();
		}
	}
}
