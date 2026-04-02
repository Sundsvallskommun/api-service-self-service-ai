package se.sundsvall.selfserviceai.integration.invoices;

import generated.se.sundsvall.invoices.Invoice;
import generated.se.sundsvall.invoices.InvoiceDetail;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

import static generated.se.sundsvall.invoices.InvoiceOrigin.COMMERCIAL;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@Component
public class InvoicesIntegration {
	private static final String ORGANIZATION_GROUP = "stadsbacken";

	private final InvoicesClient invoicesClient;

	InvoicesIntegration(InvoicesClient invoicesClient) {
		this.invoicesClient = invoicesClient;
	}

	public List<InvoiceDetail> getInvoiceDetails(final String municipalityId, final Invoice invoice) {
		return ofNullable(invoicesClient.getInvoiceDetails(municipalityId, invoice.getOrganizationNumber(), invoice.getInvoiceNumber()))
			.map(response -> ofNullable(response.getDetails()).orElse(emptyList()))
			.orElse(emptyList());
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

		if (response.getMeta().getPage() < response.getMeta().getTotalPages()) {
			getInvoices(municipalityId, partyId, fromDate, toDate, page + 1, invoices);
		}

		return invoices;
	}

	private String toString(final LocalDate date) {
		return ofNullable(date)
			.map(d -> d.format(DateTimeFormatter.ISO_LOCAL_DATE))
			.orElse(null);
	}
}
