package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record CompletionModel(

	@JsonProperty("id") UUID id,
	@JsonProperty("name") String name,
	@JsonProperty("nickname") String nickname,
	@JsonProperty("family") String family,
	@JsonProperty("token_limit") Integer tokenLimit,
	@JsonProperty("is_deprecated") Boolean isDeprecated,
	@JsonProperty("nr_billion_parameters") Integer nrBillionParameters,
	@JsonProperty("hf_link") String hfLink,
	@JsonProperty("stability") String stability,
	@JsonProperty("hosting") String hosting,
	@JsonProperty("open_source") Boolean openSource,
	@JsonProperty("description") String description,
	@JsonProperty("deployment_name") String deploymentName,
	@JsonProperty("org") String org,
	@JsonProperty("vision") boolean vision,
	@JsonProperty("reasoning") boolean reasoning,
	@JsonProperty("base_url") String baseUrl,
	@JsonProperty("is_org_enabled") boolean isOrgEnabled,
	@JsonProperty("is_org_default") boolean isOrgDefault,
	@JsonProperty("created_at") OffsetDateTime createdAt,
	@JsonProperty("updated_at") OffsetDateTime updatedAt) {
}
