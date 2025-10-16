package se.sundsvall.selfserviceai.integration.eneo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record AskResponse(

	@JsonProperty("session_id") UUID sessionId,
	@JsonProperty("question") String question,
	@JsonProperty("answer") String answer,
	@JsonProperty("files") List<FilePublic> files,
	@JsonProperty("references") List<Reference> references,
	@JsonProperty("model") CompletionModel completionModel,
	@JsonProperty("tools") Tools tools) {
}
