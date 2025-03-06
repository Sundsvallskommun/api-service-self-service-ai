package se.sundsvall.selfserviceai.integration.invoices;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.selfserviceai.integration.invoices.configuration.InvoicesConfiguration.CLIENT_ID;

import generated.se.sundsvall.invoices.InvoiceOrigin;
import generated.se.sundsvall.invoices.InvoicesResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.selfserviceai.integration.invoices.configuration.InvoicesConfiguration;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.invoices.url}", configuration = InvoicesConfiguration.class)
public interface InvoicesClient {

	/**
	 * Get invoices matching provided filters
	 *
	 * @return InstalledBaseResponse matching provided filter
	 */
	@GetMapping(path = "/{municipalityId}/{invoiceOrigin}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	InvoicesResponse getInvoices(
		@PathVariable("municipalityId") final String municipalityId,
		@PathVariable("invoiceOrigin") final InvoiceOrigin origin,
		@RequestParam("page") final int page,
		@RequestParam("limit") final int limit,
		@RequestParam("partyId") final String partyId,
		@RequestParam("organizationGroup") final String organizationGroup,
		@RequestParam("invoiceDateFrom") final LocalDate invoiceDateFrom,
		@RequestParam("invoiceDateTo") final LocalDate invoiceDateTo);
}
