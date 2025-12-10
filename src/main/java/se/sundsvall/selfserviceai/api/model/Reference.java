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
@Schema(description = "Model for reference information")
public class Reference {

	@Schema(description = "Id of the reference", examples = "d0846e7e-a860-43d0-8bc2-d0a029c23381")
	private String id;

	@Schema(description = "Metadata for the reference")
	private Metadata metadata;

	@Schema(description = "Group id to which the reference belongs", examples = "56839552-08b7-44e6-88eb-c4c524c0833b")
	private String groupId;

	@Schema(description = "Website id for the reference", examples = "56839552-08b7-44e6-88eb-c4c524c0833b")
	private String websiteId;

	@Schema(description = "Timestamp when the reference was created")
	private OffsetDateTime createdAt;

	@Schema(description = "Timestamp when the reference was last updated")
	private OffsetDateTime updatedAt;

	@Schema(description = "Score for the reference")
	private int score;
}
