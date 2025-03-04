package se.sundsvall.selfserviceai.service;

import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;

public class AssistantMapper {
	private AssistantMapper() {}

	public static QuestionResponse toQuestionResponse(final AskResponse intricResponse) {
		return toQuestionResponse(intricResponse.answer());
	}

	public static QuestionResponse toQuestionResponse(final String answer) {
		return QuestionResponse.builder()
			.withAnswer(answer)
			.build();
	}
}
