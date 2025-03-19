package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record Message(

	@JsonProperty UUID id,
	@JsonProperty String question,
	@JsonProperty String answer,
	@JsonProperty List<FilePublic> files,
	@JsonProperty OffsetDateTime createdAt,
	@JsonProperty OffsetDateTime updatedAt) {
}
