package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record AskAssistant(

	@JsonProperty("question") String question,
	@JsonProperty("stream") Boolean stream) {
}
