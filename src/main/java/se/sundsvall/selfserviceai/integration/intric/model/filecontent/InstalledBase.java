package se.sundsvall.selfserviceai.integration.intric.model.filecontent;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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
@Schema(description = "Model for installed base data sent to intric")
public class InstalledBase {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PACKAGE)
	@Builder(setterPrefix = "with")
	@Schema(description = "Model for installed base meta data sent to intric")
	public static class Metadata {

		private String name;

		private String displayName;

		private String type;

		private String value;
	}

	private String type;

	private Integer placementId;

	private LocalDate commitmentStartDate;

	private LocalDate commitmentEndDate;

	private LocalDate lastModifiedDate;

	private List<Metadata> information;
}
