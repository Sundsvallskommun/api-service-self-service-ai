package se.sundsvall.selfserviceai.integration.eneo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record SessionPublic(

	@JsonProperty("id") UUID id,
	@JsonProperty("name") String name,
	@JsonProperty("messages") List<Message> messages,
	@JsonProperty("feedback") SessionFeedback feedback,
	@JsonProperty("created_at") OffsetDateTime createdAt,
	@JsonProperty("updated_at") OffsetDateTime updatedAt) {
}
