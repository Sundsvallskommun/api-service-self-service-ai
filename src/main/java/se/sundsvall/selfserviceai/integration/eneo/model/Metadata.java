package se.sundsvall.selfserviceai.integration.eneo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record Metadata(

	@JsonProperty("embedding_model_id") UUID embeddingModelId,
	@JsonProperty("url") String url,
	@JsonProperty("title") String title,
	@JsonProperty("size") int size) {
}
