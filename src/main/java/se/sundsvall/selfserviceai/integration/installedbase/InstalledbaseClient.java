package se.sundsvall.selfserviceai.integration.installedbase;

import generated.se.sundsvall.installedbase.InstalledBaseResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.selfserviceai.integration.installedbase.configuration.InstalledbaseConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.selfserviceai.integration.installedbase.configuration.InstalledbaseConfiguration.CLIENT_ID;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.installedbase.url}", configuration = InstalledbaseConfiguration.class)
public interface InstalledbaseClient {

	/**
	 * Get installed base that sent in party has against provided organization number (within a specific municipality)
	 *
	 * @return InstalledBaseResponse matching provided filter
	 */
	@GetMapping(path = "/{municipalityId}/installedbase/{organizationNumber}", produces = APPLICATION_JSON_VALUE)
	InstalledBaseResponse getInstalledbase(
		@PathVariable("municipalityId") final String municipalityId,
		@PathVariable("organizationNumber") final String organizationNumber,
		@RequestParam("partyId") final String partyId);
}
