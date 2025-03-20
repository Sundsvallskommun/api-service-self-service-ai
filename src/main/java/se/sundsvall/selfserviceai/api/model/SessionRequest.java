package se.sundsvall.selfserviceai.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.dept44.common.validators.annotation.ValidOrganizationNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for session initiation request")
public class SessionRequest {

	@Schema(description = "The id of the party to use when retrieving the information that will form the basis of the assistant's answers", example = "5a6c3e4e-c320-4006-b448-1fd4121df828", requiredMode = REQUIRED)
	@ValidUuid
	private String partyId;

	@ArraySchema(schema = @Schema(description = "Organization id specifying the counterparty to fetch customer's engagements for", example = "5566123456"), minItems = 1)
	@NotNull
	@Size(min = 1, message = "list must contain at least 1 entry")
	private Set<@ValidOrganizationNumber(message = "list members must match the regular expression ^([1235789][\\d][2-9]\\d{7})$") String> customerEngagementOrgIds;
}
