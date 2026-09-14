package se.sundsvall.selfserviceai.integration.measurementdata;

import generated.se.sundsvall.measurementdata.Aggregation;
import generated.se.sundsvall.measurementdata.Category;
import generated.se.sundsvall.measurementdata.Data;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.selfserviceai.integration.measurementdata.configuration.MeasurementDataConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.selfserviceai.integration.measurementdata.configuration.MeasurementDataConfiguration.CLIENT_ID;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.measurement-data.url}", configuration = MeasurementDataConfiguration.class)
public interface MeasurementDataClient {

	/**
	 * Get measurement data matching provided filter
	 *
	 * @return Data matching provided filter, where each measurement serie holds the facility it belongs to
	 */
	@GetMapping(path = "/{municipalityId}/measurement-data", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	Data getMeasurementData(
		@PathVariable("municipalityId") final String municipalityId,
		@RequestParam("aggregateOn") final Aggregation aggregateOn,
		@RequestParam("fromDate") final String fromDate,
		@RequestParam("toDate") final String toDate,
		@RequestParam("partyId") final String partyId,
		@RequestParam("category") final Category category,
		@RequestParam("facilityIds") final List<String> facilityIds);
}
