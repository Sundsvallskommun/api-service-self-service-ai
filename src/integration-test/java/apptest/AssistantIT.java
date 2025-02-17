package apptest;

import static java.net.URLEncoder.encode;
import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.selfserviceai.Application;
import se.sundsvall.selfserviceai.api.model.SessionResponse;

@WireMockAppTestSuite(files = "classpath:/AssistantIT/", classes = Application.class)
class AssistantIT extends AbstractAppTest {

	private static final String PATH = "/2281/session";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void test01_createSession() {
		setupCall()
			.withServicePath(PATH)
			.withContentType(MediaType.APPLICATION_JSON)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_checkIfReadyWhenAssistantReady() throws Exception {
		// Setup session to assistant
		final var response = setupCall()
			.withServicePath(PATH)
			.withContentType(MediaType.APPLICATION_JSON)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse()
			.andReturnBody(SessionResponse.class);

		// Allow service up to 5 seconds to initialize session
		Awaitility.await()
			.atMost(Duration.ofSeconds(5l))
			.until(() -> webTestClient.get()
				.uri(PATH + "/" + response.getSessionId() + "/ready")
				.exchange()
				.expectStatus().isOk()
				.expectBody(Boolean.class)
				.returnResult()
				.getResponseBody());
	}

	@Test
	void test03_checkIfReadyWhenAssistantNotReady() {
		setupCall()
			.withServicePath(PATH + "/" + UUID.randomUUID().toString() + "/ready")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_interactWithAssistant() throws Exception {
		// Setup session to assistant
		final var response = setupCall()
			.withServicePath(PATH)
			.withContentType(MediaType.APPLICATION_JSON)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse("session_response.json")
			.sendRequestAndVerifyResponse()
			.andReturnBody(SessionResponse.class);

		// Allow service up to 5 seconds to initialize session
		Awaitility.await()
			.atMost(Duration.ofSeconds(5l))
			.until(() -> webTestClient.get()
				.uri(PATH + "/" + response.getSessionId() + "/ready")
				.exchange()
				.expectStatus().isOk()
				.expectBody(Boolean.class)
				.returnResult()
				.getResponseBody());

		// Interact with assistant
		setupCall()
			.withServicePath(PATH + "/" + response.getSessionId() + "?question=" + encode("What is the answer to the ultimate question of life, the universe, and everything?", defaultCharset()))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_deleteSession() throws Exception {
		// Setup session to assistant
		final var response = setupCall()
			.withServicePath(PATH)
			.withContentType(MediaType.APPLICATION_JSON)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse()
			.andReturnBody(SessionResponse.class);

		// Allow service up to 5 seconds to initialize session
		Awaitility.await()
			.atMost(Duration.ofSeconds(5l))
			.until(() -> webTestClient.get()
				.uri(PATH + "/" + response.getSessionId() + "/ready")
				.exchange()
				.expectStatus().isOk()
				.expectBody(Boolean.class)
				.returnResult()
				.getResponseBody());

		// Delete session
		setupCall()
			.withServicePath(PATH + "/" + response.getSessionId())
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.withExpectedResponseBodyIsNull()
			.sendRequestAndVerifyResponse();

		// Verify that the session is no longer ready
		assertThat(webTestClient.get()
			.uri(PATH + "/" + response.getSessionId() + "/ready")
			.exchange()
			.expectStatus().isOk()
			.expectBody(Boolean.class)
			.returnResult()
			.getResponseBody()).isFalse();
	}
}
