package se.sundsvall.selfserviceai.integration.measurementdata;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.selfserviceai.integration.measurementdata.configuration.MeasurementDataConfiguration.CLIENT_ID;

import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.AggregateOnEnum;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Date;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.selfserviceai.integration.measurementdata.configuration.MeasurementDataConfiguration;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.measurement-data.url}", configuration = MeasurementDataConfiguration.class)
public interface MeasurementDataClient {

	/**
	 * Get measurement data matching provided filter
	 *
	 * @return Data matching provided filter
	 */
	@GetMapping(path = "/{municipalityId}/measurement-data", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	Data getMeasurementData(
		@PathVariable("municipalityId") final String municipalityId,
		@RequestParam("aggregateOn") final AggregateOnEnum aggregateOn,
		@RequestParam("fromDate") final Date fromDate,
		@RequestParam("toDate") final Date toDate,
		@RequestParam("partyId") final String partyId,
		@RequestParam("category") final CategoryEnum category,
		@RequestParam("facilityId") final String facilityId);
}
