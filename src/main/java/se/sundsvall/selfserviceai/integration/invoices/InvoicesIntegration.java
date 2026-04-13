package se.sundsvall.selfserviceai.integration.invoices;

import generated.se.sundsvall.invoices.Invoice;
import generated.se.sundsvall.invoices.InvoiceDetail;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static generated.se.sundsvall.invoices.InvoiceOrigin.COMMERCIAL;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@Component
public class InvoicesIntegration {
	private static final Logger LOG = LoggerFactory.getLogger(InvoicesIntegration.class);
	private static final String ORGANIZATION_GROUP = "stadsbacken";

	private final InvoicesClient invoicesClient;

	InvoicesIntegration(final InvoicesClient invoicesClient) {
		this.invoicesClient = invoicesClient;
	}

	public List<InvoiceDetail> getInvoiceDetails(final String municipalityId, final Invoice invoice) {
		try {
			return ofNullable(invoicesClient.getInvoiceDetails(municipalityId, invoice.getOrganizationNumber(), invoice.getInvoiceNumber()))
				.map(response -> ofNullable(response.getDetails()).orElse(emptyList()))
				.orElse(emptyList());
		} catch (final Exception e) {
			// Some invoices (e.g., samlingsfakturor) do not have details — don't fail the whole session if a single
			// lookup fails. Return an empty list so the remaining invoices can still be processed.
			LOG.warn("Could not fetch invoice details for organization '{}' and invoice number '{}': {}",
				invoice.getOrganizationNumber(), invoice.getInvoiceNumber(), e.getMessage());
			return emptyList();
		}
	}

	public List<Invoice> getInvoices(final String municipalityId, final String partyId) {
		final var fromDate = LocalDate.now().withDayOfMonth(1).minusMonths(6); // First day of previous 6:th month
		final var toDate = LocalDate.now(); // Todays date

		return getInvoices(municipalityId, partyId, fromDate, toDate, 1, new ArrayList<>());
	}

	private List<Invoice> getInvoices(final String municipalityId, final String partyId, final LocalDate fromDate, final LocalDate toDate, final int page, final List<Invoice> invoices) {

		final var response = invoicesClient.getInvoices(municipalityId, COMMERCIAL, page, 100, partyId, ORGANIZATION_GROUP, toString(fromDate), toString(toDate));

		if (response == null) {
			return invoices;
		}

		ofNullable(response.getInvoices()).ifPresent(invoices::addAll);

		ofNullable(response.getMeta())
			.filter(meta -> ofNullable(meta.getPage()).orElse(0) < ofNullable(meta.getTotalPages()).orElse(0))
			.ifPresent(_ -> getInvoices(municipalityId, partyId, fromDate, toDate, page + 1, invoices));

		return invoices;
	}

	private String toString(final LocalDate date) {
		return ofNullable(date)
			.map(d -> d.format(DateTimeFormatter.ISO_LOCAL_DATE))
			.orElse(null);
	}
}
