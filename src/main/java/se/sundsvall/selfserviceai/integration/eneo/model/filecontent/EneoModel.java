package se.sundsvall.selfserviceai.integration.eneo.model.filecontent;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
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
@Schema(description = "Model for data sent to Eneo")
public class EneoModel {

	private String customerNumber;

	private String partyId;

	@Builder.Default
	private List<Facility> facilities = new ArrayList<>();
}
