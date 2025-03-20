package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record SessionFeedback(

	@JsonProperty("value") int value,
	@JsonProperty("text") String text) {
}
