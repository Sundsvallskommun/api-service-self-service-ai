package se.sundsvall.selfserviceai.service.mapper;

import se.sundsvall.selfserviceai.api.model.SessionResponse;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;

public class ServiceMapper {
	private ServiceMapper() {}

	public static SessionResponse toSessionResponse(final String assistantId, final AskResponse session) {
		return SessionResponse.builder()
			.withAssistantId(assistantId)
			.withSessionId(session.sessionId().toString())
			.build();
	}
}
