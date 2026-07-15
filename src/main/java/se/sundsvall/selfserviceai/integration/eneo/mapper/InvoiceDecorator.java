package se.sundsvall.selfserviceai.integration.eneo.mapper;

import generated.se.sundsvall.invoices.CustomerInvoice;
import generated.se.sundsvall.invoices.InvoiceDetail;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Address;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Invoice;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.InvoiceRow;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.selfserviceai.integration.eneo.util.ConversionUtil.toBigDecimal;
import static se.sundsvall.selfserviceai.integration.eneo.util.ConversionUtil.toBoolean;

public class InvoiceDecorator {

	static final int DECIMAL_POINTS = 2;
	static final int QUANTITY_DECIMAL_POINTS = 8;

	private InvoiceDecorator() {}

	public static void addInvoices(final List<Facility> facilities, final List<Invoice> invoices) {
		ofNullable(invoices).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(invoice -> attachToFacility(facilities, invoice));
	}

	private static void attachToFacility(final List<Facility> facilities, final Invoice invoice) {
		final var facilityIds = ofNullable(invoice.getFacilityIds()).orElse(emptyList());
		ofNullable(facilities).orElse(emptyList())
			.stream()
			.filter(facility -> facilityIds.contains(facility.getFacilityId()))
			.forEach(facility -> facility.getInvoices().add(invoice));
	}

	public static Invoice toDecoratedInvoice(final CustomerInvoice invoice) {
		return Invoice.builder()
			.withAmountVatExcluded(toBigDecimal(invoice.getAmountVatExcluded(), DECIMAL_POINTS))
			.withAmountVatIncluded(toBigDecimal(invoice.getAmountVatIncluded(), DECIMAL_POINTS))
			.withDescription(invoice.getInvoiceDescription())
			.withDueDate(invoice.getDueDate())
			.withFacilityIds(invoice.getFacilityIds())
			.withInvoiceAddress(toAddress(invoice))
			.withInvoiceName(invoice.getInvoiceName())
			.withInvoiceNumber(invoice.getInvoiceNumber())
			.withInvoiceType(ofNullable(invoice.getInvoiceType()).map(Enum::name).orElse(null))
			.withInvoicingDate(invoice.getInvoiceDate())
			.withOcrNumber(invoice.getOcrNumber())
			.withOrganizationNumber(invoice.getOrganizationNumber())
			.withPdfAvailable(toBoolean(invoice.getPdfAvailable()))
			.withRounding(toBigDecimal(invoice.getRounding(), DECIMAL_POINTS))
			.withStatus(ofNullable(invoice.getInvoiceStatus()).map(Enum::name).orElse(null))
			.withTotalAmount(toBigDecimal(invoice.getTotalAmount(), DECIMAL_POINTS))
			.withVatEligibleAmount(toBigDecimal(invoice.getVatEligibleAmount(), DECIMAL_POINTS))
			.withInvoiceRows(ofNullable(invoice.getDetails()).orElse(emptyList()).stream()
				.map(InvoiceDecorator::toInvoiceRow)
				.toList())
			.build();
	}

	static InvoiceRow toInvoiceRow(final InvoiceDetail invoiceDetail) {
		return InvoiceRow.builder()
			.withAmount(toBigDecimal(invoiceDetail.getAmount(), DECIMAL_POINTS))
			.withAmountVatExcluded(toBigDecimal(invoiceDetail.getAmountVatExcluded(), DECIMAL_POINTS))
			.withVat(toBigDecimal(invoiceDetail.getVat(), DECIMAL_POINTS))
			.withVatRate(toBigDecimal(invoiceDetail.getVatRate(), DECIMAL_POINTS))
			.withQuantity(toBigDecimal(invoiceDetail.getQuantity(), QUANTITY_DECIMAL_POINTS))
			.withUnit(invoiceDetail.getUnit())
			.withUnitPrice(toBigDecimal(invoiceDetail.getUnitPrice(), DECIMAL_POINTS))
			.withDescription(invoiceDetail.getDescription())
			.withProductCode(invoiceDetail.getProductCode())
			.withProductName(invoiceDetail.getProductName())
			.withPeriodFrom(ofNullable(invoiceDetail.getFromDate()).map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null))
			.withPeriodTo(ofNullable(invoiceDetail.getToDate()).map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null))
			.build();
	}

	static Address toAddress(final CustomerInvoice invoice) {
		return ofNullable(invoice)
			.map(source -> Address.builder()
				.withCareOf(source.getCareOf())
				.withCity(source.getCity())
				.withPostalCode(source.getPostCode())
				.withStreet(source.getStreet())
				.build())
			.orElse(null);
	}

}
