package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.selfserviceai.integration.intric.util.ConversionUtil.toBigDecimal;
import static se.sundsvall.selfserviceai.integration.intric.util.ConversionUtil.toBoolean;
import static se.sundsvall.selfserviceai.integration.intric.util.ConversionUtil.toLocalDate;

import generated.se.sundsvall.invoices.Address;
import generated.se.sundsvall.invoices.Invoice;
import java.util.List;
import java.util.Objects;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;

public class InvoiceDecorator {
	private static final int DECIMAL_POINTS = 2;

	private InvoiceDecorator() {}

	public static InstalledBase addInvoices(InstalledBase installedBase, List<Invoice> invoices) {
		ofNullable(invoices).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(invoice -> attachToFacility(installedBase, invoice));

		return installedBase;
	}

	private static void attachToFacility(InstalledBase installedBase, Invoice invoice) {
		ofNullable(installedBase.getFacilities()).orElse(emptyList())
			.stream()
			.filter(f -> f.getFacilityId().equals(invoice.getFacilityId()))
			.findFirst()
			.ifPresent(f -> f.getInvoices().add(toInvoice(invoice)));
	}

	private static se.sundsvall.selfserviceai.integration.intric.model.filecontent.Invoice toInvoice(Invoice invoice) {
		return se.sundsvall.selfserviceai.integration.intric.model.filecontent.Invoice.builder()
			.withAmountVatExcluded(toBigDecimal(invoice.getAmountVatExcluded(), DECIMAL_POINTS))
			.withAmountVatIncluded(toBigDecimal(invoice.getAmountVatIncluded(), DECIMAL_POINTS))
			.withCurrency(invoice.getCurrency())
			.withDescription(invoice.getInvoiceDescription())
			.withDueDate(toLocalDate(invoice.getDueDate()))
			.withInvoiceAddress(toAddress(invoice.getInvoiceAddress()))
			.withInvoiceName(invoice.getInvoiceName())
			.withInvoiceNumber(invoice.getInvoiceNumber())
			.withInvoiceType(invoice.getInvoiceType().name())
			.withInvoicingDate(toLocalDate(invoice.getInvoiceDate()))
			.withOcrNumber(invoice.getOcrNumber())
			.withOrganizationNumber(invoice.getOrganizationNumber())
			.withPdfAvailable(toBoolean(invoice.getPdfAvailable()))
			.withReversedVat(toBoolean(invoice.getReversedVat()))
			.withRounding(toBigDecimal(invoice.getRounding(), DECIMAL_POINTS))
			.withStatus(invoice.getInvoiceStatus().name())
			.withTotalAmount(toBigDecimal(invoice.getTotalAmount(), DECIMAL_POINTS))
			.withVat(toBigDecimal(invoice.getVat(), DECIMAL_POINTS))
			.withVatEligibleAmount(toBigDecimal(invoice.getVatEligibleAmount(), DECIMAL_POINTS))
			.build();
	}

	private static se.sundsvall.selfserviceai.integration.intric.model.filecontent.Address toAddress(Address address) {
		return ofNullable(address)
			.map(a -> se.sundsvall.selfserviceai.integration.intric.model.filecontent.Address.builder()
				.withCareOf(a.getCareOf())
				.withCity(a.getCity())
				.withPostalCode(a.getPostcode())
				.withStreet(a.getStreet())
				.build())
			.orElse(null);
	}

}
