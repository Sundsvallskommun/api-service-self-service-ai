package se.sundsvall.selfserviceai.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;

class AssistantMapperTest {

	@Test
	void toSessionResponse() {
		final var assistantId = "assistantId";
		final var sessionId = UUID.randomUUID();
		final var input = AskResponse.builder()
			.withSessionId(sessionId)
			.build();

		final var result = AssistantMapper.toSessionResponse(assistantId, input);

		assertThat(result.getAssistantId()).isEqualTo(assistantId);
		assertThat(result.getSessionId()).isEqualTo(sessionId.toString());
	}

	@Test
	void toQuestionResponseFromAskResponse() {
		// Arrange
		final var answer = "answer";
		final var input = AskResponse.builder()
			.withAnswer(answer)
			.build();

		// Act
		final var result = AssistantMapper.toQuestionResponse(input);

		// Assert
		assertThat(result.getAnswer()).isEqualTo(answer);
	}

	@Test
	void toQuestionResponseFromString() {
		// Arrange
		final var string = "string";

		// Act
		final var result = AssistantMapper.toQuestionResponse(string);

		// Assert
		assertThat(result.getAnswer()).isEqualTo(string);
	}
}
