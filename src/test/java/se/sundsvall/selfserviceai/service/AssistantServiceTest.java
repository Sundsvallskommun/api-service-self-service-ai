package se.sundsvall.selfserviceai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.Duration;
import java.util.UUID;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.selfserviceai.api.model.SessionRequest;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@InjectMocks
	private AssistantService assistantService;

	@Test
	void createSession() {
		// Act
		final var response = assistantService.createSession(MUNICIPALITY_ID);

		// Assert
		assertThat(response).isNotNull();
	}

	@Test
	void populateWithInformation() {
		// Arrange
		final var sessionId = UUID.randomUUID();

		// Act
		assertDoesNotThrow(() -> assistantService.populateWithInformation(sessionId, SessionRequest.builder().build()));
	}

	@Test
	void isSessionReadyForReadySession() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var sessionRequest = SessionRequest.builder().build();
		assistantService.populateWithInformation(sessionId, sessionRequest);

		// Assert
		Awaitility.await()
			.atMost(Duration.ofSeconds(5l))
			.until(() -> assistantService.isSessionReady(MUNICIPALITY_ID, sessionId));
	}

	@Test
	void isSessionReadyForNonReadySession() {
		// Act and assert
		assertThat(assistantService.isSessionReady(MUNICIPALITY_ID, UUID.randomUUID())).isFalse();
	}

	@Test
	void askQuestionToReadySession() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var sessionRequest = SessionRequest.builder().build();
		assistantService.populateWithInformation(sessionId, sessionRequest);

		Awaitility.await()
			.atMost(Duration.ofSeconds(5l))
			.until(() -> assistantService.isSessionReady(MUNICIPALITY_ID, sessionId));

		// Act
		final var response = assistantService.askQuestion(MUNICIPALITY_ID, sessionId, "Is this a question?");

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getAnswer()).isEqualTo("42");
	}

	@Test
	void askQuestionToNonReadySession() {
		// Act
		final var response = assistantService.askQuestion(MUNICIPALITY_ID, UUID.randomUUID(), "Is this a question?");

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getAnswer()).isEqualTo("Assistant is not ready yet");
	}

	@Test
	void deleteSession() {
		// Arrange
		final var sessionId = assistantService.createSession(MUNICIPALITY_ID);
		final var sessionRequest = SessionRequest.builder().build();
		assistantService.populateWithInformation(sessionId, sessionRequest);
		Awaitility.await()
			.atMost(Duration.ofSeconds(5l))
			.until(() -> assistantService.isSessionReady(MUNICIPALITY_ID, sessionId));

		// Assert active session
		assertThat(assistantService.isSessionReady(MUNICIPALITY_ID, sessionId)).isTrue();

		// Act
		assistantService.deleteSession(MUNICIPALITY_ID, sessionId);

		// Assert session removed
		assertThat(assistantService.isSessionReady(MUNICIPALITY_ID, sessionId)).isFalse();

	}
}
