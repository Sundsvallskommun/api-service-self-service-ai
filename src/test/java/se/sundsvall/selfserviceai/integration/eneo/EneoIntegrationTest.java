package se.sundsvall.selfserviceai.integration.eneo;

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
import se.sundsvall.selfserviceai.integration.eneo.mapper.EneoMapper;
import se.sundsvall.selfserviceai.integration.eneo.model.AskAssistant;
import se.sundsvall.selfserviceai.integration.eneo.model.AskResponse;
import se.sundsvall.selfserviceai.integration.eneo.model.FilePublic;
import se.sundsvall.selfserviceai.integration.eneo.model.InformationFile;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.EneoModel;
import se.sundsvall.selfserviceai.service.util.JsonBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EneoIntegrationTest {

	@Mock
	private EneoClient eneoClientMock;

	@Mock
	private EneoMapper eneoMapperMock;

	@Mock
	private JsonBuilder jsonBuilderMock;

	@InjectMocks
	private EneoIntegration integration;

	@Captor
	private ArgumentCaptor<AskAssistant> askAssistantCaptor;

	@AfterEach
	void verifyNoMoreMockInteraction() {
		verifyNoMoreInteractions(eneoClientMock, eneoMapperMock, jsonBuilderMock);
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

		when(eneoMapperMock.toAskAssistant(input)).thenReturn(AskAssistant.builder().withQuestion(input).build());
		when(eneoClientMock.askAssistant(eq(assistantId), askAssistantCaptor.capture())).thenReturn(response);

		final var result = integration.askAssistant(assistantId, input);
		final var askAssistant = askAssistantCaptor.getValue();
		assertThat(askAssistant.question()).isEqualTo(input);
		assertThat(result).isEqualTo(response);

		verify(eneoMapperMock).toAskAssistant(input);
		verify(eneoClientMock).askAssistant(assistantId, askAssistant);
	}

	/**
	 * Test scenario where the client throws an exception
	 */
	@Test
	void askAssistant_2() {
		final var assistantId = "assistantId";
		final var input = "input";
		final var exception = new RuntimeException("Something went wrong");

		when(eneoMapperMock.toAskAssistant(input)).thenReturn(AskAssistant.builder().withQuestion(input).build());
		when(eneoClientMock.askAssistant(eq(assistantId), askAssistantCaptor.capture())).thenThrow(exception);

		final var e = assertThrows(RuntimeException.class, () -> integration.askAssistant(assistantId, input));
		final var askAssistant = askAssistantCaptor.getValue();
		assertThat(askAssistant.question()).isEqualTo(input);
		assertThat(e).isSameAs(exception);

		verify(eneoMapperMock).toAskAssistant(input);
		verify(eneoClientMock).askAssistant(assistantId, askAssistant);
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void askFollowUp_1() {
		final var assistantId = "assistantId";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var fileId = "9c55c2be-9739-4af4-89dd-201a507ce261";
		final var input = "input";
		final var response = AskResponse.builder()
			.withSessionId(UUID.fromString(sessionId))
			.withQuestion(input)
			.withAnswer("answer")
			.build();

		when(eneoClientMock.askFollowUp(eq(assistantId), eq(sessionId), askAssistantCaptor.capture())).thenReturn(response);
		when(eneoMapperMock.toAskAssistant(input, List.of(fileId))).thenReturn(AskAssistant.builder()
			.withQuestion(input)
			.withFiles(List.of(FilePublic.builder()
				.withId(UUID.fromString(fileId))
				.build()))
			.build());

		final var result = integration.askFollowUp(assistantId, sessionId, input, List.of(fileId));
		final var askAssistant = askAssistantCaptor.getValue();

		assertThat(result).isPresent().hasValue(response);
		assertThat(askAssistant.question()).isEqualTo(input);
		assertThat(askAssistant.files()).hasSize(1).satisfiesExactly(f -> {
			assertThat(f.id()).isEqualTo(UUID.fromString(fileId));
		});

		verify(eneoMapperMock).toAskAssistant(input, List.of(fileId));
		verify(eneoClientMock).askFollowUp(assistantId, sessionId, askAssistant);
	}

	/**
	 * Test scenario where the client throws an exception
	 */
	@Test
	void askFollowUp_2() {
		final var assistantId = "assistantId";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var fileId = "9c55c2be-9739-4af4-89dd-201a507ce261";
		final var input = "input";

		when(eneoMapperMock.toAskAssistant(input, List.of(fileId))).thenCallRealMethod();
		when(eneoClientMock.askFollowUp(eq(assistantId), eq(sessionId), askAssistantCaptor.capture())).thenThrow(new RuntimeException("Something went wrong"));

		final var result = integration.askFollowUp(assistantId, sessionId, input, List.of(fileId));
		final var askAssistant = askAssistantCaptor.getValue();
		assertThat(result).isEmpty();
		assertThat(askAssistant.question()).isEqualTo(input);
		assertThat(askAssistant.files()).hasSize(1).satisfiesExactly(fp -> {
			assertThat(fp.id()).isEqualTo(UUID.fromString(fileId));
		});

		verify(eneoMapperMock).toAskAssistant(input, List.of(fileId));
		verify(eneoClientMock).askFollowUp(assistantId, sessionId, askAssistant);
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void uploadFile_1() {
		final var eneoModel = EneoModel.builder().build();
		final var id = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var filePublic = FilePublic.builder()
			.withId(UUID.fromString(id))
			.withName("name")
			.withMimeType("mimeType")
			.withSize(123)
			.build();

		when(eneoMapperMock.toInformationFile(any())).thenReturn(InformationFile.create());
		when(eneoClientMock.uploadFile(any(MultipartFile.class))).thenReturn(filePublic);

		final var result = integration.uploadFile(eneoModel);

		assertThat(result).isEqualTo(UUID.fromString(id));

		verify(jsonBuilderMock).toJsonString(eneoModel);
		verify(eneoMapperMock).toInformationFile(any());
		verify(eneoClientMock).uploadFile(any(MultipartFile.class));
	}

	/**
	 * Test scenario where the client throws an exception
	 */
	@Test
	void uploadFile_2() {
		final var eneoModel = EneoModel.builder().build();
		final var exception = new RuntimeException("Something went wrong");

		when(eneoMapperMock.toInformationFile(any())).thenReturn(InformationFile.create());
		when(eneoClientMock.uploadFile(any(MultipartFile.class))).thenThrow(exception);

		final var e = assertThrows(RuntimeException.class, () -> integration.uploadFile(eneoModel));

		assertThat(e).isSameAs(exception);

		verify(jsonBuilderMock).toJsonString(eneoModel);
		verify(eneoMapperMock).toInformationFile(any());
		verify(eneoClientMock).uploadFile(any(MultipartFile.class));
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void deleteFile_1() {
		final var fileId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";

		final var result = integration.deleteFile(fileId);

		assertThat(result).isTrue();

		verify(eneoClientMock).deleteFile(fileId);
	}

	/**
	 * Test scenario where client throws an exception
	 */
	@Test
	void deleteFile_2() {
		final var fileId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var exception = new RuntimeException("Something went wrong");

		doThrow(exception).when(eneoClientMock).deleteFile(fileId);

		final var result = integration.deleteFile(fileId);

		assertThat(result).isFalse();

		verify(eneoClientMock).deleteFile(fileId);
	}

	/**
	 * Test scenario where everything works as expected
	 */
	@Test
	void deleteSession_1() {
		final var assistantId = "6d04a0bc-54a1-4877-a81b-3dd2c2063509";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";

		final var result = integration.deleteSession(assistantId, sessionId);

		assertThat(result).isTrue();

		verify(eneoClientMock).deleteSession(assistantId, sessionId);
	}

	/**
	 * Test scenario where client throws an exception
	 */
	@Test
	void deleteSession_2() {
		final var assistantId = "6d04a0bc-54a1-4877-a81b-3dd2c2063509";
		final var sessionId = "2d357dcf-6180-48de-a9e8-3ad74b757c84";
		final var exception = new RuntimeException("Something went wrong");

		doThrow(exception).when(eneoClientMock).deleteSession(assistantId, sessionId);

		final var result = integration.deleteSession(assistantId, sessionId);

		assertThat(result).isFalse();

		verify(eneoClientMock).deleteSession(assistantId, sessionId);
	}
}
