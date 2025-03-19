package se.sundsvall.selfserviceai.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.selfserviceai.Application;
import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.api.model.SessionRequest;
import se.sundsvall.selfserviceai.api.model.SessionResponse;
import se.sundsvall.selfserviceai.service.AssistantService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SessionResourceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@MockitoBean
	private AssistantService mockService;

	@Autowired
	private WebTestClient webTestClient;

	@AfterEach
	void noMoreMockInteractions() {
		verifyNoMoreInteractions(mockService);
	}

	@Test
	void createSession() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var customerEngagementOrgId = "5566123456";
		final var partyId = UUID.randomUUID().toString();
		final var body = SessionRequest.builder()
			.withCustomerEngagementOrgId(customerEngagementOrgId)
			.withPartyId(partyId)
			.build();

		when(mockService.createSession(MUNICIPALITY_ID, partyId)).thenReturn(sessionId);

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path("/{municipalityId}/session").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.bodyValue(body)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(SessionResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().extracting(SessionResponse::getSessionId).isEqualTo(sessionId.toString());
		verify(mockService).createSession(MUNICIPALITY_ID, partyId);
		verify(mockService).populateWithInformation(sessionId, body);
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void isAssistantReady(boolean isReady) {
		// Arrange
		final var sessionId = UUID.randomUUID();

		when(mockService.isSessionReady(MUNICIPALITY_ID, sessionId)).thenReturn(isReady);

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/{municipalityId}/session/{sessionId}/ready").build(Map.of("municipalityId", MUNICIPALITY_ID, "sessionId", sessionId.toString())))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Boolean.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().isEqualTo(isReady);
		verify(mockService).isSessionReady(MUNICIPALITY_ID, sessionId);
	}

	@Test
	void askAssistant() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var question = "question?";
		final var answer = "answer!";

		when(mockService.askQuestion(MUNICIPALITY_ID, sessionId, question)).thenReturn(QuestionResponse.builder().withAnswer(answer).build());

		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder
				.path("/{municipalityId}/session/{sessionId}")
				.queryParam("question", question)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "sessionId", sessionId.toString())))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(QuestionResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().extracting(QuestionResponse::getAnswer).isEqualTo(answer);
		verify(mockService).askQuestion(MUNICIPALITY_ID, sessionId, question);
	}

	@Test
	void askAssistantWhenNoAnswer() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var question = "question?";

		// Act
		webTestClient.get()
			.uri(builder -> builder
				.path("/{municipalityId}/session/{sessionId}")
				.queryParam("question", question)
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "sessionId", sessionId.toString())))
			.exchange()
			.expectStatus().isNoContent()
			.expectHeader().contentType(ALL)
			.expectBody().isEmpty()
			.getResponseBody();

		// Assert and verify
		verify(mockService).askQuestion(MUNICIPALITY_ID, sessionId, question);
	}

	@Test
	void deleteSession() {
		// Arrange
		final var sessionId = UUID.randomUUID();

		// Act
		webTestClient.delete()
			.uri(builder -> builder.path("/{municipalityId}/session/{sessionId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "sessionId", sessionId.toString())))
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		// Assert and verify
		verify(mockService).deleteSessionById(MUNICIPALITY_ID, sessionId);
	}
}
