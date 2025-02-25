package se.sundsvall.selfserviceai.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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
@Schema(description = "Model for facility data sent to intric")
public class Facility {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PACKAGE)
	@Builder(setterPrefix = "with")
	@Schema(description = "Model for facility meta data sent to intric")
	public static class Metadata {

		private String name;

		private String displayName;

		private String type;

		private String value;
	}

	private String facilityId;

	private String type;

	private Integer placementId;

	private LocalDate commitmentStartDate;

	private LocalDate commitmentEndDate;

	private LocalDate lastModifiedDate;

	private Address address;

	private List<Metadata> information;

	@Builder.Default
	private List<Agreement> agreements = new ArrayList<>();

	@Builder.Default
	private List<MeasurementData> measurements = new ArrayList<>();

	@Builder.Default
	private List<Invoice> invoices = new ArrayList<>();
}
