package se.sundsvall.selfserviceai.service.mapper;

import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.api.model.SessionResponse;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;

public class AssistantMapper {
	private AssistantMapper() {}

	public static SessionResponse toSessionResponse(final String assistantId, final AskResponse session) {
		return SessionResponse.builder()
			.withAssistantId(assistantId)
			.withSessionId(session.sessionId().toString())
			.build();
	}

	public static QuestionResponse toQuestionResponse(final AskResponse intricResponse) {
		return toQuestionResponse(intricResponse.answer());
	}

	public static QuestionResponse toQuestionResponse(final String answer) {
		return QuestionResponse.builder()
			.withAnswer(answer)
			.build();
	}
}
