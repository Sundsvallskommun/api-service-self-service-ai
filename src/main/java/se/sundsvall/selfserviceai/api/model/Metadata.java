package se.sundsvall.selfserviceai.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for model metadata information")
public class Metadata {

	@Schema(description = "Id for the embedding model", examples = "d0846e7e-a860-43d0-8bc2-d0a029c23381")
	private String embeddingModelId;

	@Schema(description = "Url for the embedding model", examples = "http://some.url")
	private String url;

	@Schema(description = "Title of the embeddning model", examples = "Title")
	private String title;

	@Schema(description = "Size")
	private int size;
}
