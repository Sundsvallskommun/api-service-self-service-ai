package se.sundsvall.selfserviceai.integration.eneo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record Assistant(

	@JsonProperty("id") UUID id,
	@JsonProperty("handle") String handle) {
}
