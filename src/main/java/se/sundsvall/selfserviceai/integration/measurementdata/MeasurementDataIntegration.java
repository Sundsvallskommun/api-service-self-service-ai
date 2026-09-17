package se.sundsvall.selfserviceai.integration.measurementdata;

import generated.se.sundsvall.measurementdata.Category;
import generated.se.sundsvall.measurementdata.Data;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;

import static generated.se.sundsvall.measurementdata.Aggregation.MONTH;
import static generated.se.sundsvall.measurementdata.Category.DISTRICT_HEATING;
import static generated.se.sundsvall.measurementdata.Category.ELECTRICITY;
import static java.time.LocalDate.now;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Component
public class MeasurementDataIntegration {
	/** The categories that are implemented in the backend, as it answers 501 for the other ones. */
	public static final List<Category> CATEGORIES = List.of(DISTRICT_HEATING, ELECTRICITY);

	private final MeasurementDataClient measurementDataClient;

	MeasurementDataIntegration(MeasurementDataClient measurementDataClient) {
		this.measurementDataClient = measurementDataClient;
	}

	/**
	 * Fetches measurement data of one category for all provided facilities in a single request.
	 *
	 * The service accepts all facilities in a single request and returns one measurement serie per facility, so the
	 * facilities must not be requested one by one — a customer with many facilities would otherwise produce hundreds of
	 * requests for every created session.
	 *
	 * A failure is propagated to the caller, which decides what a missing category means for the session. The exception
	 * is a category that the backend answers 501 for, which is treated as "no data".
	 *
	 * @param  municipalityId id of the municipality that the facilities belong to
	 * @param  partyId        party id of the customer that owns the facilities
	 * @param  facilities     the facilities to fetch measurement data for
	 * @param  category       the category to fetch measurement data for
	 * @return                the measurement data for the category, or null if there is nothing to fetch or the category is
	 *                        not implemented by the backend
	 */
	public Data getMeasurementData(String municipalityId, String partyId, List<Facility> facilities, Category category) {
		final var facilityIds = ofNullable(facilities).orElse(emptyList())
			.stream()
			.map(Facility::getFacilityId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();

		if (facilityIds.isEmpty()) {
			return null;
		}

		try {
			return measurementDataClient.getMeasurementData(
				municipalityId,
				MONTH,
				now(systemDefault()).minusMonths(12).atStartOfDay(systemDefault()).toOffsetDateTime().format(ISO_DATE_TIME), // Fetch data from 12 month back
				now(systemDefault()).plusDays(1).atStartOfDay(systemDefault()).toOffsetDateTime().format(ISO_DATE_TIME), // Fetch data to midnight of today
				partyId,
				category,
				facilityIds);

		} catch (final ThrowableProblem e) {
			if (Objects.equals(BAD_GATEWAY, e.getStatus()) && ofNullable(e.getDetail()).orElse("").contains("category '%s', status=501 Not Implemented".formatted(category))) {
				return null;
			}
			throw e;
		}
	}
}
