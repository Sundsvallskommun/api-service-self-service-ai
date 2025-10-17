package se.sundsvall.selfserviceai.integration.eneo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record Reference(

	@JsonProperty("id") UUID id,
	@JsonProperty("metadata") Metadata metadata,
	@JsonProperty("group_id") UUID groupId,
	@JsonProperty("website_id") UUID websiteId,
	@JsonProperty("created_at") OffsetDateTime createdAt,
	@JsonProperty("updated_at") OffsetDateTime updatedAt,
	@JsonProperty("score") int score) {
}
