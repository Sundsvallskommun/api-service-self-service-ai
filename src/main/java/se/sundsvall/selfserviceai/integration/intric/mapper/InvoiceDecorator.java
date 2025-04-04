package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.selfserviceai.integration.intric.util.ConversionUtil.toBigDecimal;
import static se.sundsvall.selfserviceai.integration.intric.util.ConversionUtil.toBoolean;

import generated.se.sundsvall.invoices.InvoiceDetail;
import generated.se.sundsvall.invoices.InvoiceStatus;
import generated.se.sundsvall.invoices.InvoiceType;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Address;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Invoice;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InvoiceRow;

public class InvoiceDecorator {

	static final int DECIMAL_POINTS = 2;

	private InvoiceDecorator() {}

	public static void addInvoices(List<Facility> facilities, List<Invoice> invoices) {
		ofNullable(invoices).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(invoice -> attachToFacility(facilities, invoice));
	}

	private static void attachToFacility(List<Facility> facilities, Invoice invoice) {
		ofNullable(facilities).orElse(emptyList())
			.stream()
			.filter(f -> f.getFacilityId().equals(invoice.getFacilityId()))
			.findFirst()
			.ifPresent(f -> f.getInvoices().add(invoice));
	}

	public static Invoice toDecoratedInvoice(final generated.se.sundsvall.invoices.Invoice invoice, final List<InvoiceDetail> invoiceDetails) {
		return Invoice.builder()
			.withAmountVatExcluded(toBigDecimal(invoice.getAmountVatExcluded(), DECIMAL_POINTS))
			.withAmountVatIncluded(toBigDecimal(invoice.getAmountVatIncluded(), DECIMAL_POINTS))
			.withCurrency(invoice.getCurrency())
			.withDescription(invoice.getInvoiceDescription())
			.withDueDate(invoice.getDueDate())
			.withFacilityId(invoice.getFacilityId())
			.withInvoiceAddress(toAddress(invoice.getInvoiceAddress()))
			.withInvoiceName(invoice.getInvoiceName())
			.withInvoiceNumber(invoice.getInvoiceNumber())
			.withInvoiceType(ofNullable(invoice.getInvoiceType()).map(InvoiceType::name).orElse(null))
			.withInvoicingDate(invoice.getInvoiceDate())
			.withOcrNumber(invoice.getOcrNumber())
			.withOrganizationNumber(invoice.getOrganizationNumber())
			.withPdfAvailable(toBoolean(invoice.getPdfAvailable()))
			.withReversedVat(toBoolean(invoice.getReversedVat()))
			.withRounding(toBigDecimal(invoice.getRounding(), DECIMAL_POINTS))
			.withStatus(ofNullable(invoice.getInvoiceStatus()).map(InvoiceStatus::name).orElse(null))
			.withTotalAmount(toBigDecimal(invoice.getTotalAmount(), DECIMAL_POINTS))
			.withVat(toBigDecimal(invoice.getVat(), DECIMAL_POINTS))
			.withVatEligibleAmount(toBigDecimal(invoice.getVatEligibleAmount(), DECIMAL_POINTS))
			.withInvoiceRows(invoiceDetails.stream()
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
			.withQuantity(toBigDecimal(invoiceDetail.getQuantity(), DECIMAL_POINTS))
			.withUnit(invoiceDetail.getUnit())
			.withUnitPrice(toBigDecimal(invoiceDetail.getUnitPrice(), DECIMAL_POINTS))
			.withDescription(invoiceDetail.getDescription())
			.withProductCode(invoiceDetail.getProductCode())
			.withProductName(invoiceDetail.getProductName())
			.withPeriodFrom(ofNullable(invoiceDetail.getFromDate()).map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null))
			.withPeriodTo(ofNullable(invoiceDetail.getToDate()).map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null))
			.build();
	}

	static Address toAddress(final generated.se.sundsvall.invoices.Address address) {
		return ofNullable(address)
			.map(a -> Address.builder()
				.withCareOf(a.getCareOf())
				.withCity(a.getCity())
				.withPostalCode(a.getPostcode())
				.withStreet(a.getStreet())
				.build())
			.orElse(null);
	}

}
