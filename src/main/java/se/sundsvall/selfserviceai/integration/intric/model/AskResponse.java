package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record AskResponse(

	@JsonProperty("session_id") UUID sessionId,
	@JsonProperty("question") String question,
	@JsonProperty("answer") String answer) {
}
