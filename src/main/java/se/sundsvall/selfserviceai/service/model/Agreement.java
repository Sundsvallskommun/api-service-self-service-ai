package se.sundsvall.selfserviceai.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for agreement data sent to intric")
public class Agreement {

	private String agreementId;

	private String description;

	private String category;

	private LocalDate fromDate;

	private boolean bound;

	private String bindingRule;
}
