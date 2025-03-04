package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record FilePublic(

	@JsonProperty UUID id,
	@JsonProperty String name,
	@JsonProperty String mimeType,
	@JsonProperty Integer size,
	@JsonProperty OffsetDateTime createdAt,
	@JsonProperty OffsetDateTime updatedAt) {
}
