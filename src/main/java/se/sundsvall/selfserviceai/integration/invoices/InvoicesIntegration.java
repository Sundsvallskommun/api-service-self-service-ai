package se.sundsvall.selfserviceai.integration.invoices;

import generated.se.sundsvall.invoices.CustomerInvoice;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import se.sundsvall.selfserviceai.integration.invoices.configuration.InvoicesProperties;

import static java.time.ZoneId.systemDefault;
import static java.util.Optional.ofNullable;

@Component
public class InvoicesIntegration {

	private final InvoicesClient invoicesClient;
	private final List<String> organizationNumbers;

	InvoicesIntegration(final InvoicesClient invoicesClient, final InvoicesProperties properties) {
		this.invoicesClient = invoicesClient;
		this.organizationNumbers = properties.organizationNumbers();
	}

	public List<CustomerInvoice> getInvoices(final String municipalityId, final String partyId) {
		final var periodFrom = LocalDate.now(systemDefault()).withDayOfMonth(1).minusMonths(6); // First day of previous 6:th month
		final var periodTo = LocalDate.now(systemDefault()); // Todays date

		return getInvoices(municipalityId, partyId, periodFrom, periodTo, 1, new ArrayList<>());
	}

	private List<CustomerInvoice> getInvoices(final String municipalityId, final String partyId, final LocalDate periodFrom, final LocalDate periodTo, final int page, final List<CustomerInvoice> invoices) {

		// Scoping to the "stadsbacken" organization group is achieved via the configured organization numbers, as the
		// customer-invoices endpoint does not offer an organization-group filter.
		final var response = invoicesClient.getInvoicesForCustomer(municipalityId, List.of(partyId), organizationNumbers, toString(periodFrom), toString(periodTo), page, 100);

		if (response == null) {
			return invoices;
		}

		ofNullable(response.getInvoices()).ifPresent(invoices::addAll);

		ofNullable(response.getMeta())
			.filter(meta -> ofNullable(meta.getPage()).orElse(0) < ofNullable(meta.getTotalPages()).orElse(0))
			.ifPresent(_ -> getInvoices(municipalityId, partyId, periodFrom, periodTo, page + 1, invoices));

		return invoices;
	}

	private String toString(final LocalDate date) {
		return ofNullable(date)
			.map(d -> d.format(DateTimeFormatter.ISO_LOCAL_DATE))
			.orElse(null);
	}
}
