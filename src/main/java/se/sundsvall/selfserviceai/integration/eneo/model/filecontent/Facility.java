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
@Schema(description = "Model for facility data sent to Eneo")
public class Facility {

	private String facilityId;

	private Address address;

	@Builder.Default
	private List<InstalledBase> installedBases = new ArrayList<>();

	@Builder.Default
	private List<Agreement> agreements = new ArrayList<>();

	@Builder.Default
	private List<MeasurementData> measurements = new ArrayList<>();

	@Builder.Default
	private List<Invoice> invoices = new ArrayList<>();
}
