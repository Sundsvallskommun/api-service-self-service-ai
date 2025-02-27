package se.sundsvall.selfserviceai.integration.intric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.selfserviceai.integration.intric.model.AskAssistant;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;
import se.sundsvall.selfserviceai.integration.intric.model.FilePublic;

@ExtendWith(MockitoExtension.class)
class IntricIntegrationTest {

	@Mock
	private IntricClient intricClientMock;

	@InjectMocks
	private IntricIntegration integration;

	@Captor
	private ArgumentCaptor<AskAssistant> askAssistantCaptor;

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void askAssistant_1() {
		final var assistantId = "assistantId";
		final var input = "input";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var answer = "answer";
		final var response = AskResponse.builder()
			.withSessionId(UUID.fromString(sessionId))
			.withQuestion(input)
			.withAnswer(answer)
			.build();
		when(intricClientMock.askAssistant(eq(assistantId), askAssistantCaptor.capture())).thenReturn(response);

		final var result = integration.askAssistant(assistantId, input);

		final var askAssistant = askAssistantCaptor.getValue();
		assertThat(askAssistant.question()).isEqualTo(input);

		assertThat(result).isEqualTo(response);
		verify(intricClientMock).askAssistant(assistantId, askAssistant);
	}

	/**
	 * Test scenario where the client throws an exception
	 */
	@Test
	void askAssistant_2() {
		final var assistantId = "assistantId";
		final var input = "input";
		when(intricClientMock.askAssistant(eq(assistantId), askAssistantCaptor.capture())).thenThrow(new RuntimeException("Something went wrong"));

		final var result = integration.askAssistant(assistantId, input);

		final var askAssistant = askAssistantCaptor.getValue();
		assertThat(askAssistant.question()).isEqualTo(input);

		assertThat(result).isNull();
		verify(intricClientMock).askAssistant(assistantId, askAssistant);
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void askFollowUp_1() {
		final var assistantId = "assistantId";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var input = "input";
		final var fileReferences = List.of("fileId");
		final var response = AskResponse.builder()
			.withSessionId(UUID.fromString(sessionId))
			.withQuestion(input)
			.withAnswer("answer")
			.build();

		when(intricClientMock.askFollowUp(eq(assistantId), eq(sessionId), askAssistantCaptor.capture())).thenReturn(response);

		final var result = integration.askFollowUp(assistantId, sessionId, input, fileReferences);

		final var askAssistant = askAssistantCaptor.getValue();
		assertThat(askAssistant.question()).isEqualTo(input);
		assertThat(askAssistant.files()).isEqualTo(fileReferences);

		assertThat(result).isEqualTo(response);
		verify(intricClientMock).askFollowUp(assistantId, sessionId, askAssistant);
	}

	/**
	 * Test scenario where the client throws an exception
	 */
	@Test
	void askFollowUp_2() {
		final var assistantId = "assistantId";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var input = "input";
		final var fileReferences = List.of("fileId");

		when(intricClientMock.askFollowUp(eq(assistantId), eq(sessionId), askAssistantCaptor.capture())).thenThrow(new RuntimeException("Something went wrong"));

		final var result = integration.askFollowUp(assistantId, sessionId, input, fileReferences);

		final var askAssistant = askAssistantCaptor.getValue();
		assertThat(askAssistant.question()).isEqualTo(input);
		assertThat(askAssistant.files()).isEqualTo(fileReferences);

		assertThat(result).isNull();
		verify(intricClientMock).askFollowUp(assistantId, sessionId, askAssistant);
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void uploadFile_1() {
		final var multiPartFileMock = Mockito.mock(MultipartFile.class);
		final var id = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var filePublic = FilePublic.builder()
			.withId(UUID.fromString(id))
			.withName("name")
			.withMimeType("mimeType")
			.withSize(123)
			.build();
		when(intricClientMock.uploadFile(multiPartFileMock)).thenReturn(filePublic);

		final var result = integration.uploadFile(multiPartFileMock);

		assertThat(result).isEqualTo(UUID.fromString(id));

		verify(intricClientMock).uploadFile(multiPartFileMock);
	}

	/**
	 * Test scenario where the client throws an exception
	 */
	@Test
	void uploadFile_2() {
		final var multiPartFileMock = Mockito.mock(MultipartFile.class);
		when(intricClientMock.uploadFile(multiPartFileMock)).thenThrow(new RuntimeException("Something went wrong"));

		final var result = integration.uploadFile(multiPartFileMock);

		assertThat(result).isNull();
		verify(intricClientMock).uploadFile(multiPartFileMock);
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void deleteFile() {
		final var fileId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";

		integration.deleteFile(fileId);

		verify(intricClientMock).deleteFile(fileId);
	}

}
