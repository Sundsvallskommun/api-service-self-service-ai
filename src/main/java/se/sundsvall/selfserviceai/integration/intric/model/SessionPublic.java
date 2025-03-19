package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record SessionPublic(

	@JsonProperty UUID id,
	@JsonProperty String name,
	@JsonProperty List<Message> messages,
	@JsonProperty OffsetDateTime createdAt,
	@JsonProperty OffsetDateTime updatedAt) {
}
