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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.selfserviceai.Application;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;

@WireMockAppTestSuite(files = "classpath:/AssistantIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class AssistantIT extends AbstractAppTest {

	private static final String PATH = "/2281/session";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private FileRepository fileRepository;

	private TransactionTemplate transactionTemplate;

	@BeforeEach
	void setUp() {
		transactionTemplate = new TransactionTemplate(transactionManager);
	}

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
		setupCall()
			.withServicePath(PATH + "/4dc21d5e-8a70-45fb-b225-367fcd383a2e/ready")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
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
	void test04_interactWithAssistant() {
		final var sessionId = "4dc21d5e-8a70-45fb-b225-367fcd383a2e";

		setupCall()
			.withServicePath(PATH + "/" + sessionId + "?question=" + encode("What is the answer to the ultimate question of life, the universe and everything?", defaultCharset()))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_interactWithNonReadyAssistant() {
		final var sessionId = "a6602aba-0b21-4abf-a869-60c583570129";

		setupCall()
			.withServicePath(PATH + "/" + sessionId + "?question=" + encode("What is the answer to the ultimate question of life, the universe, and everything?", defaultCharset()))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_deleteSession() {
		final var sessionId = "158cfabe-1c3d-433c-b71f-1c909beaa291";
		final var fileId = "811bcd0e-fe12-448e-85c5-2248a4a12e6d";
		
		transactionTemplate.executeWithoutResult(status -> {
			final var session = sessionRepository.getReferenceById(sessionId);

			// Verify that the session exists and is ready for interaction
			assertThat(session.getInitialized()).isNotNull();
			assertThat(session.getFiles()).hasSize(1);
		});

		// Delete session (asynchronously)
		setupCall()
			.withServicePath(PATH + "/" + sessionId)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.withExpectedResponseBodyIsNull()
			.sendRequestAndVerifyResponse();

		// Verify that the session is no longer ready (i.e. is deleted)
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.ignoreExceptions()
			.until(() -> transactionTemplate.execute(status -> {
				assertThat(sessionRepository.findById(sessionId)).isNotPresent();
				assertThat(fileRepository.findById(fileId)).isEmpty();

				return true;
			}));
	}
}
