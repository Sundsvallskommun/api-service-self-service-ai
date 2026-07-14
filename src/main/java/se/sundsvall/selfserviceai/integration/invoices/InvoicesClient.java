package se.sundsvall.selfserviceai.integration.invoices;

import generated.se.sundsvall.invoices.CustomerInvoicesResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.selfserviceai.integration.invoices.configuration.InvoicesConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.selfserviceai.integration.invoices.configuration.InvoicesConfiguration.CLIENT_ID;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.invoices.url}", configuration = InvoicesConfiguration.class, dismiss404 = true)
public interface InvoicesClient {

	/**
	 * Get commercial invoices for one or more customers matching provided filters. Each returned invoice carries its
	 * invoice detail rows embedded.
	 *
	 * @return CustomerInvoicesResponse matching provided filter
	 */
	@GetMapping(path = "/{municipalityId}/COMMERCIAL/customers/invoices", produces = APPLICATION_JSON_VALUE)
	CustomerInvoicesResponse getInvoicesForCustomer(
		@PathVariable final String municipalityId,
		@RequestParam("partyIds") final List<String> partyIds,
		@RequestParam("organizationNumbers") final List<String> organizationNumbers,
		@RequestParam("periodFrom") final String periodFrom,
		@RequestParam("periodTo") final String periodTo,
		@RequestParam("page") final int page,
		@RequestParam("limit") final int limit);
}
