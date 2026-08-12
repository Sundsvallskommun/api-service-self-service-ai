package se.sundsvall.selfserviceai.service;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;

import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

/**
 * Verifies that the pessimistic lock taken by {@code SessionRepository.findForUpdateBySessionId} serializes the removal
 * of a session against another transaction that connects a file to it.
 *
 * Without the lock the removal would read the files before the other transaction commits, conclude that the session is
 * empty and remove it - which fails with a foreign key violation on {@code fk_session_file} once the file row lands.
 * With the lock the removal has to wait, sees the file, and keeps the session for a later attempt.
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
class SessionPersistenceServiceLockTest {

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

	@Test
	void finalizeDeletionWaitsForTransactionThatConnectsAFile() throws Exception {
		final var concurrentFileId = UUID.randomUUID().toString();
		final var fileConnected = new CountDownLatch(1);
		final var allowCommit = new CountDownLatch(1);

		// populateWithInformation locks the session and connects its file, but has not committed yet
		final var population = CompletableFuture.runAsync(() -> connectFileWithinLockedTransaction(concurrentFileId, fileConnected, allowCommit));
		assertThat(fileConnected.await(10, SECONDS)).isTrue();

		final var removal = CompletableFuture.runAsync(() -> sessionPersistenceService.finalizeDeletion(SESSION_ID, emptyList(), true));

		// The removal must not be able to act on the session while the other transaction holds it
		assertThatThrownBy(() -> removal.get(2, SECONDS)).isInstanceOf(TimeoutException.class);

		allowCommit.countDown();
		population.get(10, SECONDS);

		// Once the file row is committed the removal proceeds, sees the file and keeps the session - without failing
		removal.get(10, SECONDS);

		assertThat(fileRepository.existsById(concurrentFileId)).isTrue();
		assertThat(sessionRepository.findById(SESSION_ID)).hasValueSatisfying(session -> assertThat(session.getStatus())
			.startsWith("Failed to delete session, filter logs on log id '"));
	}

	/**
	 * Locks the session row and connects a file to it on a connection of its own, keeping the transaction open until the
	 * test allows it to commit.
	 */
	private void connectFileWithinLockedTransaction(final String fileId, final CountDownLatch fileConnected, final CountDownLatch allowCommit) {
		try (var connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);

			try (var lock = connection.prepareStatement("select session_id from session where session_id = ? for update")) {
				lock.setString(1, SESSION_ID);
				lock.executeQuery();
			}

			try (var insert = connection.prepareStatement("insert into file(file_id, session_id) values(?, ?)")) {
				insert.setString(1, fileId);
				insert.setString(2, SESSION_ID);
				insert.executeUpdate();
			}

			fileConnected.countDown();
			if (!allowCommit.await(10, SECONDS)) {
				throw new IllegalStateException("The test never allowed the transaction to commit");
			}

			connection.commit();
		} catch (final SQLException e) {
			throw new IllegalStateException(e);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(e);
		}
	}
}
