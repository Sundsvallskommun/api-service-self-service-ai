package se.sundsvall.selfserviceai.integration.measurementdata;

import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.AggregateOnEnum.MONTH;
import static java.time.LocalDate.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.BAD_GATEWAY;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.zalando.problem.ThrowableProblem;

@Component
public class MeasurementDataIntegration {
	private final MeasurementDataClient measurementDataClient;

	MeasurementDataIntegration(MeasurementDataClient measurementDataClient) {
		this.measurementDataClient = measurementDataClient;
	}

	public List<Data> getMeasurementData(String municipalityId, String partyId, List<Agreement> agreements) {
		return ofNullable(agreements).orElse(emptyList())
			.stream()
			.filter(MeasurementDataIntegration::hasProcessableCategory) // Only fetch measurements for the categories that exists in measurement-data service
			.map(agreement -> getMeasurementData(municipalityId, partyId, agreement))
			.filter(Objects::nonNull) // null is returned if category is not implemented in measurement data service and must be filtered out from response
			.toList();
	}

	private Data getMeasurementData(String municipalityId, String partyId, Agreement agreement) {
		try {
			return measurementDataClient.getMeasurementData(
				municipalityId,
				MONTH,
				now().minusMonths(12).atStartOfDay(systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME), // Fetch data from 12 month back
				now().plusDays(1).atStartOfDay(systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME), // Fetch data to midnight of today
				partyId,
				CategoryEnum.fromValue(agreement.getCategory().toString()),
				agreement.getFacilityId());

		} catch (final ThrowableProblem e) {
			if (Objects.equals(BAD_GATEWAY, e.getStatus()) && e.getDetail().contains("category '%s', status=501 Not Implemented".formatted(CategoryEnum.fromValue(agreement.getCategory().toString())))) {
				return null;
			}

			throw e;
		}
	}

	private static boolean hasProcessableCategory(Agreement agreement) {
		return Objects.nonNull(agreement.getCategory()) &&
			List.of(CategoryEnum.values())
				.stream()
				.anyMatch(member -> agreement.getCategory().name().equals(member.name()));
	}
}
