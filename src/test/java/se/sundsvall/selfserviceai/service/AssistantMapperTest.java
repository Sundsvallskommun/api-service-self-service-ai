package se.sundsvall.selfserviceai.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;

class AssistantMapperTest {

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
