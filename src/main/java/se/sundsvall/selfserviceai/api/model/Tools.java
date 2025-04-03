package se.sundsvall.selfserviceai.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for tools information")
public class Tools {
	@ArraySchema(schema = @Schema(description = "Assistants used when answering asked question", implementation = Assistant.class))
	private List<Assistant> assistants;
}
