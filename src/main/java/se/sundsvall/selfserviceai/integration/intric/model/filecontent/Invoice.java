package se.sundsvall.selfserviceai.integration.intric.model.filecontent;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
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
@Schema(description = "Model for invoice data sent to intric")
public class Invoice {

	private BigDecimal amountVatIncluded;

	private BigDecimal amountVatExcluded;

	private String currency;

	private String description;

	private LocalDate dueDate;

	private String facilityId;

	private Address invoiceAddress;

	private LocalDate invoicingDate;

	private String invoiceName;

	private String invoiceNumber;

	private String invoiceType;

	private String ocrNumber;

	private String organizationNumber;

	private boolean pdfAvailable;

	private boolean reversedVat;

	private BigDecimal rounding;

	private String status;

	private BigDecimal totalAmount;

	private BigDecimal vat;

	private BigDecimal vatEligibleAmount;

	@Builder.Default
	private List<InvoiceRow> invoiceRows = new ArrayList<>();
}
