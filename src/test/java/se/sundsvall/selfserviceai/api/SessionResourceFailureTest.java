package se.sundsvall.selfserviceai.api;

import static java.util.stream.Collectors.toCollection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.selfserviceai.Application;
import se.sundsvall.selfserviceai.api.model.SessionRequest;
import se.sundsvall.selfserviceai.service.AssistantService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SessionResourceFailureTest {
	private static final String MUNICIPALITY_ID = "2281";

	@MockitoBean
	private AssistantService mockService;

	@Autowired
	private WebTestClient webTestClient;

	@AfterEach
	void noMockInteractions() {
		verifyNoInteractions(mockService);
	}

	// ========================================================
	// createAssistantSession
	// ========================================================

	@Test
	void createAssistantSessionWithFaultyPathParameters() {
		// Arrange
		final var body = SessionRequest.builder()
			.withCustomerEngagementOrgIds(Set.of("5566123456"))
			.withPartyId(UUID.randomUUID().toString())
			.build();

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path("/{municipalityId}/session").build(Map.of("municipalityId", "invalid")))
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(
					tuple("createAssistantSession.municipalityId", "not a valid municipality ID"));
		});
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"invalid"
	})
	@NullSource
	void createAssistantSessionWithFaultyOrEmptyBodyValues(String value) {
		// Arrange
		final var body = SessionRequest.builder()
			.withCustomerEngagementOrgIds(Stream.of(value).collect(toCollection(HashSet::new)))
			.withPartyId(value)
			.build();

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path("/{municipalityId}/session").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(
					tuple("customerEngagementOrgIds[]", "list members must match the regular expression ^([1235789][\\d][2-9]\\d{7})$"),
					tuple("partyId", "not a valid UUID"));
		});
	}

	@Test
	void createAssistantSessionWithEmptyBody() {
		// Arrange
		final var body = SessionRequest.builder().build();

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path("/{municipalityId}/session").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(
					tuple("customerEngagementOrgIds", "must not be null"),
					tuple("partyId", "not a valid UUID"));
		});
	}

	@Test
	void createAssistantSessionWithEmptyList() {
		// Arrange
		final var body = SessionRequest.builder()
			.withCustomerEngagementOrgIds(Set.of())
			.build();

		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path("/{municipalityId}/session").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(
					tuple("customerEngagementOrgIds", "list must contain at least 1 entry"),
					tuple("partyId", "not a valid UUID"));
		});
	}

	@Test
	void createAssistantSessionWithNoBody() {
		// Act
		final var response = webTestClient.post()
			.uri(builder -> builder.path("/{municipalityId}/session").build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getTitle()).isEqualTo("Bad Request");
			assertThat(r.getDetail()).isEqualTo("""
				Required request body is missing: org.springframework.http.ResponseEntity<se.sundsvall.selfserviceai.api.model.SessionResponse> \
				se.sundsvall.selfserviceai.api.AssistantResource.createAssistantSession(java.lang.String,se.sundsvall.selfserviceai.api.model.SessionRequest)\
				""");
		});
	}

	// ========================================================
	// isAssistantActive
	// ========================================================

	@Test
	void isAssistantActiveWithFaultyPathParameters() {
		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/{municipalityId}/session/{sessionId}/ready").build(Map.of("municipalityId", "invalid", "sessionId", "invalid")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(
					tuple("isAssistantReady.municipalityId", "not a valid municipality ID"),
					tuple("isAssistantReady.sessionId", "not a valid UUID"));
		});
	}

	// ========================================================
	// askAssistant
	// ========================================================

	@ParameterizedTest
	@ValueSource(strings = {
		"", " "
	})
	@NullSource
	void askAssistantWithFaultyPathParameters(String question) {
		// Act
		final var response = webTestClient.get()
			.uri(builder -> builder.path("/{municipalityId}/session/{sessionId}")
				.queryParam("question", question)
				.build(Map.of("municipalityId", "invalid", "sessionId", "invalid")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(
					tuple("askAssistant.municipalityId", "not a valid municipality ID"),
					tuple("askAssistant.sessionId", "not a valid UUID"),
					tuple("askAssistant.question", "must not be blank"));
		});
	}

	// ========================================================
	// deleteAssistantSession
	// ========================================================

	@Test
	void deleteAssistantSessionWithFaultyPathParameters() {
		// Act
		final var response = webTestClient.delete()
			.uri(builder -> builder.path("/{municipalityId}/session/{sessionId}").build(Map.of("municipalityId", "invalid", "sessionId", "invalid")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().satisfies(r -> {
			assertThat(r.getTitle()).isEqualTo("Constraint Violation");
			assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(r.getViolations()).extracting(Violation::getField, Violation::getMessage)
				.containsExactlyInAnyOrder(
					tuple("deleteAssistantSession.municipalityId", "not a valid municipality ID"),
					tuple("deleteAssistantSession.sessionId", "not a valid UUID"));
		});
	}
}
