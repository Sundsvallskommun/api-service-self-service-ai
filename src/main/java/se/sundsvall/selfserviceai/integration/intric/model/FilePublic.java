package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record FilePublic(

	@JsonProperty("id") UUID id,
	@JsonProperty("name") String name,
	@JsonProperty("mime_type") String mimeType,
	@JsonProperty("size") Integer size,
	@JsonProperty("created_at") OffsetDateTime createdAt,
	@JsonProperty("updated_at") OffsetDateTime updatedAt,
	@JsonProperty("transcription") String transcription) {
}
