package se.sundsvall.selfserviceai.integration.eneo.model.filecontent;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for invoice rows data sent to Eneo")
public class InvoiceRow {

	private BigDecimal amount;

	private BigDecimal amountVatExcluded;

	private BigDecimal vat;

	private BigDecimal vatRate;

	private BigDecimal quantity;

	private String unit;

	private BigDecimal unitPrice;

	private String description;

	private String productCode;

	private String productName;

	private String periodFrom;

	private String periodTo;
}
