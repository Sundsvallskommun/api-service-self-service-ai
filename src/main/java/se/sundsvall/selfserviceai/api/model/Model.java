package se.sundsvall.selfserviceai.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for completion model information")
public class Model {

	@Schema(description = "Id of the model", examples = "1f717b73-c676-4316-112e-cda43650d832")
	private String id;

	@Schema(description = "Name for the model", examples = "gpt-4o-mini-azure")
	private String name;

	@Schema(description = "Nickname for the model", examples = "GPT-4o mini (Azure)")
	private String nickname;

	@Schema(description = "Family to which the model belongs", examples = "azure")
	private String family;

	@Schema(description = "Token limit")
	private Integer tokenLimit;

	@Schema(description = "Signal if model is deprecated or not")
	private Boolean deprecated;

	@Schema(description = "Nr billion parameters")
	private int nrBillionParameters;

	@Schema(description = "Hf link for model", examples = "http://some.address")
	private String hfLink;

	@Schema(description = "Stability of the model", examples = "stable")
	private String stability;

	@Schema(description = "Hosting for the model", examples = "swe")
	private String hosting;

	@Schema(description = "Signal if the model is open source or not")
	private Boolean openSource;

	@Schema(description = "Description for the model", examples = "Microsoft Azure's hosted version of the compact GPT-4 Omni model, offering faster processing with advanced capabilities.")
	private String description;

	@Schema(description = "Deployment name for the model", examples = "gpt-4o-mini")
	private String deploymentName;

	@Schema(description = "Organization owning the model", examples = "Microsoft")
	private String org;

	@Schema(description = "Vision setting for the model")
	private boolean vision;

	@Schema(description = "Reasoning setting for the model")
	private boolean reasoning;

	@Schema(description = "Base url for the model", examples = "http://some.url")
	private String baseUrl;

	@Schema(description = "Organization enabled setting for the model")
	private boolean orgEnabled;

	@Schema(description = "Organization default setting for the model")
	private boolean orgDefault;

	@Schema(description = "Timestamp when model was created")
	private OffsetDateTime createdAt;

	@Schema(description = "Timestamp when model was last updated")
	private OffsetDateTime updatedAt;
}
