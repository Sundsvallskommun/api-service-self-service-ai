package se.sundsvall.selfserviceai.integration.eneo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record Message(

	@JsonProperty("id") UUID id,
	@JsonProperty("question") String question,
	@JsonProperty("answer") String answer,
	@JsonProperty("completion_model") CompletionModel completionModel,
	@JsonProperty("references") List<Reference> references,
	@JsonProperty("files") List<FilePublic> files,
	@JsonProperty("tools") Tools tools,
	@JsonProperty("created_at") OffsetDateTime createdAt,
	@JsonProperty("updated_at") OffsetDateTime updatedAt) {
}
