package se.sundsvall.selfserviceai.integration.intric.model;

import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record AskResponse(

	UUID sessionId,
	String question,
	String answer) {
}
