package se.sundsvall.selfserviceai.integration.agreement;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.selfserviceai.integration.agreement.configuration.AgreementConfiguration.CLIENT_ID;

import generated.se.sundsvall.agreement.AgreementResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.selfserviceai.integration.agreement.configuration.AgreementConfiguration;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.agreement.url}", configuration = AgreementConfiguration.class)
public interface AgreementClient {

	/**
	 * Get customer agreements matching provided filter
	 *
	 * @return AgreementResponse with agreements matching sent in parameters
	 */
	@GetMapping(path = "/{municipalityId}/agreements/{partyId}", produces = APPLICATION_JSON_VALUE)
	AgreementResponse getAgreements(
		@PathVariable("municipalityId") final String municipalityId,
		@PathVariable("partyId") final String partyId,
		@RequestParam("onlyActive") final boolean onlyActive);
}
