package se.sundsvall.selfserviceai.integration.measurementdata;

import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.AggregateOnEnum.MONTH;
import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum.DISTRICT_HEATING;
import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum.ELECTRICITY;
import static java.time.LocalDate.now;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.BAD_GATEWAY;

import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;

@Component
public class MeasurementDataIntegration {
	private static final List<CategoryEnum> VALID_CATEGORIES = List.of(DISTRICT_HEATING, ELECTRICITY);

	private final MeasurementDataClient measurementDataClient;

	MeasurementDataIntegration(MeasurementDataClient measurementDataClient) {
		this.measurementDataClient = measurementDataClient;
	}

	public List<Data> getMeasurementData(String municipalityId, String partyId, List<Facility> facilities) {
		return ofNullable(facilities).orElse(emptyList())
			.stream()
			.map(Facility::getFacilityId)
			.map(facilityId -> getMeasurementData(municipalityId, partyId, facilityId))
			.flatMap(List::stream)
			.toList();
	}

	private List<Data> getMeasurementData(String municipalityId, String partyId, String facilityId) {
		return List.of(CategoryEnum.values()).stream()
			.filter(VALID_CATEGORIES::contains) // Only use the categories that are implemented, as the backend throws exception if other ones used
			.map(category -> getMeasurementData(municipalityId, partyId, facilityId, category))
			.filter(Objects::nonNull)
			.toList();
	}

	private Data getMeasurementData(String municipalityId, String partyId, String facilityId, CategoryEnum category) {
		try {
			return measurementDataClient.getMeasurementData(
				municipalityId,
				MONTH,
				now().minusMonths(12).atStartOfDay(systemDefault()).toOffsetDateTime().format(ISO_DATE_TIME), // Fetch data from 12 month back
				now().plusDays(1).atStartOfDay(systemDefault()).toOffsetDateTime().format(ISO_DATE_TIME), // Fetch data to midnight of today
				partyId,
				category,
				facilityId);

		} catch (final ThrowableProblem e) {
			if (Objects.equals(BAD_GATEWAY, e.getStatus()) && e.getDetail().contains("category '%s', status=501 Not Implemented".formatted(CategoryEnum.fromValue(category.toString())))) {
				return null;
			}

			throw e;
		}
	}
}
