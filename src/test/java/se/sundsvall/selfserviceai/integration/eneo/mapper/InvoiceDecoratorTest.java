package se.sundsvall.selfserviceai.integration.eneo.mapper;

import generated.se.sundsvall.invoices.CustomerInvoice;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createCustomerInvoice;
import static se.sundsvall.selfserviceai.TestFactory.createFacility;
import static se.sundsvall.selfserviceai.TestFactory.createInvoice;
import static se.sundsvall.selfserviceai.TestFactory.createInvoiceDetail;
import static se.sundsvall.selfserviceai.integration.eneo.mapper.InvoiceDecorator.DECIMAL_POINTS;
import static se.sundsvall.selfserviceai.integration.eneo.mapper.InvoiceDecorator.QUANTITY_DECIMAL_POINTS;
import static se.sundsvall.selfserviceai.integration.eneo.util.ConversionUtil.toBigDecimal;
import static se.sundsvall.selfserviceai.integration.eneo.util.ConversionUtil.toBoolean;

class InvoiceDecoratorTest {

	@Test
	void addInvoices() {
		var facilities = List.of(createFacility(facility -> facility.setFacilityId("123")));

		var invoice1 = createInvoice(invoice -> invoice.setFacilityIds(List.of("123")));
		var invoice2 = createInvoice(invoice -> invoice.setFacilityIds(List.of("321")));
		var invoice3 = createInvoice(invoice -> invoice.setFacilityIds(List.of("123")));
		var invoices = List.of(invoice1, invoice2, invoice3);

		InvoiceDecorator.addInvoices(facilities, invoices);

		var facility = facilities.getFirst();
		assertThat(facility.getInvoices()).isNotNull().hasSize(2).containsExactlyInAnyOrder(invoice1, invoice3);
	}

	@Test
	void addInvoicesMatchingMultipleFacilities() {
		var facilities = List.of(
			createFacility(facility -> facility.setFacilityId("123")),
			createFacility(facility -> facility.setFacilityId("456")));

		var invoice = createInvoice(i -> i.setFacilityIds(List.of("123", "456")));

		InvoiceDecorator.addInvoices(facilities, List.of(invoice));

		assertThat(facilities.get(0).getInvoices()).containsExactly(invoice);
		assertThat(facilities.get(1).getInvoices()).containsExactly(invoice);
	}

	@Test
	void addInvoicesNoMatches() {
		var facilities = List.of(createFacility(facility -> facility.setFacilityId("123")));

		var invoice1 = createInvoice(invoice -> invoice.setFacilityIds(List.of("321")));
		var invoice2 = createInvoice(invoice -> invoice.setFacilityIds(List.of("321")));
		var invoice3 = createInvoice(invoice -> invoice.setFacilityIds(List.of("321")));
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
		var invoice = createCustomerInvoice();

		var result = InvoiceDecorator.toDecoratedInvoice(invoice);

		assertThat(result.getAmountVatExcluded()).isEqualTo(toBigDecimal(invoice.getAmountVatExcluded(), DECIMAL_POINTS));
		assertThat(result.getAmountVatIncluded()).isEqualTo(toBigDecimal(invoice.getAmountVatIncluded(), DECIMAL_POINTS));
		assertThat(result.getDescription()).isEqualTo(invoice.getInvoiceDescription());
		assertThat(result.getDueDate()).isEqualTo(invoice.getDueDate());
		assertThat(result.getFacilityIds()).isEqualTo(invoice.getFacilityIds());
		assertThat(result.getInvoiceAddress()).usingRecursiveComparison().isEqualTo(InvoiceDecorator.toAddress(invoice));
		assertThat(result.getInvoiceName()).isEqualTo(invoice.getInvoiceName());
		assertThat(result.getInvoiceNumber()).isEqualTo(invoice.getInvoiceNumber());
		assertThat(result.getInvoiceType()).isEqualTo(invoice.getInvoiceType().name());
		assertThat(result.getInvoicingDate()).isEqualTo(invoice.getInvoiceDate());
		assertThat(result.getOcrNumber()).isEqualTo(invoice.getOcrNumber());
		assertThat(result.getOrganizationNumber()).isEqualTo(invoice.getOrganizationNumber());
		assertThat(result.isPdfAvailable()).isEqualTo(toBoolean(invoice.getPdfAvailable()));
		assertThat(result.getRounding()).isEqualTo(toBigDecimal(invoice.getRounding(), DECIMAL_POINTS));
		assertThat(result.getStatus()).isEqualTo(invoice.getInvoiceStatus().name());
		assertThat(result.getTotalAmount()).isEqualTo(toBigDecimal(invoice.getTotalAmount(), DECIMAL_POINTS));
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
		assertThat(result.getQuantity()).isEqualTo(toBigDecimal(invoiceDetail.getQuantity(), QUANTITY_DECIMAL_POINTS));
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
		var invoice = new CustomerInvoice()
			.careOf("careOf")
			.city("city")
			.postCode("postcode")
			.street("street");

		var result = InvoiceDecorator.toAddress(invoice);

		assertThat(result.getCareOf()).isEqualTo(invoice.getCareOf());
		assertThat(result.getCity()).isEqualTo(invoice.getCity());
		assertThat(result.getPostalCode()).isEqualTo(invoice.getPostCode());
		assertThat(result.getStreet()).isEqualTo(invoice.getStreet());
	}

}
