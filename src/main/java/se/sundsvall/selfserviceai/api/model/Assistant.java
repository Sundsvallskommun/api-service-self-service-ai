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
@Schema(description = "Model for assistant information")
public class Assistant {

	@Schema(description = "Id of the assistant", example = "9406e9e3-e2bf-4b5a-9237-2925b396f096")
	private String id;

	@Schema(description = "Readable handle name for the assistant", example = "Assistant name")
	private String handle;
}
