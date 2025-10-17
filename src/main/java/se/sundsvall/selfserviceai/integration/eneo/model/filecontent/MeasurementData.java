package se.sundsvall.selfserviceai.integration.eneo.model.filecontent;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
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
@Schema(description = "Model for measurement data sent to Eneo")
public class MeasurementData {

	private String category;

	private String measurementType;

	private BigDecimal value;

	private String unit;

	private OffsetDateTime timestamp;
}
