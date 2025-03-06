package se.sundsvall.selfserviceai.integration.intric;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.selfserviceai.integration.intric.mapper.JsonBuilder;
import se.sundsvall.selfserviceai.integration.intric.model.AskAssistant;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;
import se.sundsvall.selfserviceai.integration.intric.model.FilePublic;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;

@ExtendWith(MockitoExtension.class)
class IntricIntegrationTest {

	@Mock
	private IntricClient intricClientMock;

	@Mock
	private JsonBuilder jsonBuilderMock;

	@InjectMocks
	private IntricIntegration integration;

	@Captor
	private ArgumentCaptor<AskAssistant> askAssistantCaptor;

	@AfterEach
	void verifyNoMoreMockInteraction() {
		verifyNoMoreInteractions(intricClientMock, jsonBuilderMock);
	}

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
		final var exception = new RuntimeException("Something went wrong");

		when(intricClientMock.askAssistant(eq(assistantId), askAssistantCaptor.capture())).thenThrow(exception);

		final var e = assertThrows(RuntimeException.class, () -> integration.askAssistant(assistantId, input));

		final var askAssistant = askAssistantCaptor.getValue();
		assertThat(askAssistant.question()).isEqualTo(input);

		assertThat(e).isSameAs(exception);
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

		assertThat(result).isPresent().hasValue(response);
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

		assertThat(result).isEmpty();
		verify(intricClientMock).askFollowUp(assistantId, sessionId, askAssistant);
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void uploadFile_1() throws Exception {
		final var installedBase = InstalledBase.builder().build();
		final var id = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var filePublic = FilePublic.builder()
			.withId(UUID.fromString(id))
			.withName("name")
			.withMimeType("mimeType")
			.withSize(123)
			.build();
		when(intricClientMock.uploadFile(any(MultipartFile.class))).thenReturn(filePublic);

		final var result = integration.uploadFile(installedBase);

		assertThat(result).isEqualTo(UUID.fromString(id));

		verify(jsonBuilderMock).toJsonString(installedBase);
		verify(intricClientMock).uploadFile(any(MultipartFile.class));
	}

	/**
	 * Test scenario where the client throws an exception
	 */
	@Test
	void uploadFile_2() throws Exception {
		final var installedBase = InstalledBase.builder().build();
		final var exception = new RuntimeException("Something went wrong");
		when(intricClientMock.uploadFile(any(MultipartFile.class))).thenThrow(exception);

		final var e = assertThrows(RuntimeException.class, () -> integration.uploadFile(installedBase));

		assertThat(e).isSameAs(exception);
		verify(jsonBuilderMock).toJsonString(installedBase);
		verify(intricClientMock).uploadFile(any(MultipartFile.class));
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void deleteFile_1() {
		final var fileId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";

		final var result = integration.deleteFile(fileId);

		verify(intricClientMock).deleteFile(fileId);
		assertThat(result).isTrue();
	}

	/**
	 * Test scenario where client throws an exception
	 */
	@Test
	void deleteFile_2() {
		final var fileId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var exception = new RuntimeException("Something went wrong");

		doThrow(exception).when(intricClientMock).deleteFile(fileId);

		final var result = integration.deleteFile(fileId);

		verify(intricClientMock).deleteFile(fileId);
		assertThat(result).isFalse();
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void deleteSession_1() {
		final var assistantId = "6d04a0bc-54a1-4877-a81b-3dd2c2063509";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";

		final var result = integration.deleteSession(assistantId, sessionId);

		verify(intricClientMock).deleteSession(assistantId, sessionId);
		assertThat(result).isTrue();
	}

	/**
	 * Test scenario where client throws an exception
	 */
	@Test
	void deleteSession_2() {

		final var assistantId = "6d04a0bc-54a1-4877-a81b-3dd2c2063509";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var exception = new RuntimeException("Something went wrong");

		doThrow(exception).when(intricClientMock).deleteSession(assistantId, sessionId);

		final var result = integration.deleteSession(assistantId, sessionId);

		verify(intricClientMock).deleteSession(assistantId, sessionId);
		assertThat(result).isFalse();
	}
}
