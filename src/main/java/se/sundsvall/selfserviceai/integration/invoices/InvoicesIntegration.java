package se.sundsvall.selfserviceai.integration.invoices;

import static generated.se.sundsvall.invoices.InvoiceOrigin.COMMERCIAL;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.NOT_FOUND;

import generated.se.sundsvall.invoices.Invoice;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.zalando.problem.ThrowableProblem;

@Component
public class InvoicesIntegration {
	private static final String ORGANIZATION_GROUP = "stadsbacken";

	private final InvoicesClient invoicesClient;

	InvoicesIntegration(InvoicesClient invoicesClient) {
		this.invoicesClient = invoicesClient;
	}

	public List<Invoice> getInvoices(String municipalityId, String partyId) {
		final var fromDate = LocalDate.now().withDayOfMonth(1).minusMonths(6); // First day of previous 6:th month
		final var toDate = LocalDate.now(); // Todays date

		try {
			return getInvoices(municipalityId, partyId, fromDate, toDate, 1, new ArrayList<>());
		} catch (final ThrowableProblem e) {
			if (Objects.equals(NOT_FOUND, e.getStatus())) {
				return emptyList();
			}
			throw e;
		}
	}

	private List<Invoice> getInvoices(String municipalityId, String partyId, LocalDate fromDate, LocalDate toDate, int page, List<Invoice> invoices) {

		final var response = invoicesClient.getInvoices(municipalityId, COMMERCIAL, page, 100, partyId, ORGANIZATION_GROUP, fromDate, toDate);
		ofNullable(response.getInvoices()).ifPresent(invoices::addAll);

		if (response.getMeta().getPage() < response.getMeta().getTotalPages()) {
			getInvoices(municipalityId, partyId, fromDate, toDate, page + 1, invoices);
		}

		return invoices;
	}
}
