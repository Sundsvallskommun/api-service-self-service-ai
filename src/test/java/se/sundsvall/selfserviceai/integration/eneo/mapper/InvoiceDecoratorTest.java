package se.sundsvall.selfserviceai.integration.eneo.mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createAddress;
import static se.sundsvall.selfserviceai.TestFactory.createFacility;
import static se.sundsvall.selfserviceai.TestFactory.createGeneratedInvoice;
import static se.sundsvall.selfserviceai.TestFactory.createInvoice;
import static se.sundsvall.selfserviceai.TestFactory.createInvoiceDetail;
import static se.sundsvall.selfserviceai.integration.eneo.mapper.InvoiceDecorator.DECIMAL_POINTS;
import static se.sundsvall.selfserviceai.integration.eneo.util.ConversionUtil.toBigDecimal;
import static se.sundsvall.selfserviceai.integration.eneo.util.ConversionUtil.toBoolean;

class InvoiceDecoratorTest {

	@Test
	void addInvoices() {
		var facilities = List.of(createFacility(facility -> facility.setFacilityId("123")));

		var invoice1 = createInvoice(invoice -> invoice.setFacilityId("123"));
		var invoice2 = createInvoice(invoice -> invoice.setFacilityId("321"));
		var invoice3 = createInvoice(invoice -> invoice.setFacilityId("123"));
		var invoices = List.of(invoice1, invoice2, invoice3);

		InvoiceDecorator.addInvoices(facilities, invoices);

		var facility = facilities.getFirst();
		assertThat(facility.getInvoices()).isNotNull().hasSize(2).containsExactlyInAnyOrder(invoice1, invoice3);
	}

	@Test
	void addInvoicesNoMatches() {
		var facilities = List.of(createFacility(facility -> facility.setFacilityId("123")));

		var invoice1 = createInvoice(invoice -> invoice.setFacilityId("321"));
		var invoice2 = createInvoice(invoice -> invoice.setFacilityId("321"));
		var invoice3 = createInvoice(invoice -> invoice.setFacilityId("321"));
		var invoices = List.of(invoice1, invoice2, invoice3);

		InvoiceDecorator.addInvoices(facilities, invoices);

		var facility = facilities.getFirst();
		assertThat(facility.getInvoices()).isNotNull().isEmpty();
	}

	@Test
	void addInvoicesFromNull() {
		var facilities = List.of(createFacility(facility -> facility.setFacilityId("123")));

		InvoiceDecorator.addInvoices(facilities, null);

		var facility = facilities.getFirst();
		assertThat(facility.getInvoices()).isNotNull().isEmpty();
	}

	@Test
	void toDecoratedInvoice() {
		generated.se.sundsvall.invoices.Invoice invoice = createGeneratedInvoice();
		var invoiceDetail1 = createInvoiceDetail();
		var invoiceDetail2 = createInvoiceDetail();
		var invoiceDetail3 = createInvoiceDetail();
		var invoiceDetails = List.of(invoiceDetail1, invoiceDetail2, invoiceDetail3);

		var result = InvoiceDecorator.toDecoratedInvoice(invoice, invoiceDetails);

		assertThat(result.getAmountVatExcluded()).isEqualTo(toBigDecimal(invoice.getAmountVatExcluded(), DECIMAL_POINTS));
		assertThat(result.getAmountVatIncluded()).isEqualTo(toBigDecimal(invoice.getAmountVatIncluded(), DECIMAL_POINTS));
		assertThat(result.getCurrency()).isEqualTo(invoice.getCurrency());
		assertThat(result.getDescription()).isEqualTo(invoice.getInvoiceDescription());
		assertThat(result.getDueDate()).isEqualTo(invoice.getDueDate());
		assertThat(result.getFacilityId()).isEqualTo(invoice.getFacilityId());
		assertThat(result.getInvoiceAddress()).usingRecursiveComparison().isEqualTo(InvoiceDecorator.toAddress(invoice.getInvoiceAddress()));
		assertThat(result.getInvoiceName()).isEqualTo(invoice.getInvoiceName());
		assertThat(result.getInvoiceNumber()).isEqualTo(invoice.getInvoiceNumber());
		assertThat(result.getInvoiceType()).isEqualTo(invoice.getInvoiceType().name());
		assertThat(result.getInvoicingDate()).isEqualTo(invoice.getInvoiceDate());
		assertThat(result.getOrganizationNumber()).isEqualTo(invoice.getOrganizationNumber());
		assertThat(result.isPdfAvailable()).isEqualTo(toBoolean(invoice.getPdfAvailable()));
		assertThat(result.isReversedVat()).isEqualTo(toBoolean(invoice.getReversedVat()));
		assertThat(result.getRounding()).isEqualTo(toBigDecimal(invoice.getRounding(), DECIMAL_POINTS));
		assertThat(result.getStatus()).isEqualTo(invoice.getInvoiceStatus().name());
		assertThat(result.getTotalAmount()).isEqualTo(toBigDecimal(invoice.getTotalAmount(), DECIMAL_POINTS));
		assertThat(result.getVat()).isEqualTo(toBigDecimal(invoice.getVat(), DECIMAL_POINTS));
		assertThat(result.getVatEligibleAmount()).isEqualTo(toBigDecimal(invoice.getVatEligibleAmount(), DECIMAL_POINTS));
		assertThat(result.getInvoiceRows()).hasSize(3);
	}

	@Test
	void toInvoiceRow() {
		var invoiceDetail = createInvoiceDetail();

		var result = InvoiceDecorator.toInvoiceRow(invoiceDetail);

		assertThat(result.getAmount()).isEqualTo(toBigDecimal(invoiceDetail.getAmount(), DECIMAL_POINTS));
		assertThat(result.getAmountVatExcluded()).isEqualTo(toBigDecimal(invoiceDetail.getAmountVatExcluded(), DECIMAL_POINTS));
		assertThat(result.getVat()).isEqualTo(toBigDecimal(invoiceDetail.getVat(), DECIMAL_POINTS));
		assertThat(result.getVatRate()).isEqualTo(toBigDecimal(invoiceDetail.getVatRate(), DECIMAL_POINTS));
		assertThat(result.getQuantity()).isEqualTo(toBigDecimal(invoiceDetail.getQuantity(), DECIMAL_POINTS));
		assertThat(result.getUnit()).isEqualTo(invoiceDetail.getUnit());
		assertThat(result.getUnitPrice()).isEqualTo(toBigDecimal(invoiceDetail.getUnitPrice(), DECIMAL_POINTS));
		assertThat(result.getDescription()).isEqualTo(invoiceDetail.getDescription());
		assertThat(result.getProductCode()).isEqualTo(invoiceDetail.getProductCode());
		assertThat(result.getProductName()).isEqualTo(invoiceDetail.getProductName());
		assertThat(result.getPeriodFrom()).isEqualTo(invoiceDetail.getFromDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
		assertThat(result.getPeriodTo()).isEqualTo(invoiceDetail.getToDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
	}

	@Test
	void toAddress() {
		var address = createAddress();

		var result = InvoiceDecorator.toAddress(address);

		assertThat(result.getCareOf()).isEqualTo(address.getCareOf());
		assertThat(result.getCity()).isEqualTo(address.getCity());
		assertThat(result.getPostalCode()).isEqualTo(address.getPostcode());
		assertThat(result.getStreet()).isEqualTo(address.getStreet());
	}

}
