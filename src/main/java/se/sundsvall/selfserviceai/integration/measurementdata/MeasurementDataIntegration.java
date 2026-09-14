package se.sundsvall.selfserviceai.integration.measurementdata;

import generated.se.sundsvall.measurementdata.Category;
import generated.se.sundsvall.measurementdata.Data;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

@Component
public class MeasurementDataIntegration {
	private static final Logger LOG = LoggerFactory.getLogger(MeasurementDataIntegration.class);
	private static final List<Category> VALID_CATEGORIES = List.of(DISTRICT_HEATING, ELECTRICITY); // Only the categories that are implemented, as the backend answers 501 for the other ones

	private final MeasurementDataClient measurementDataClient;

	MeasurementDataIntegration(MeasurementDataClient measurementDataClient) {
		this.measurementDataClient = measurementDataClient;
	}

	/**
	 * Fetches measurement data for all provided facilities, with one request per category.
	 *
	 * The service accepts all facilities in a single request and returns one measurement serie per facility, so the
	 * facilities must not be requested one by one — a customer with many facilities would otherwise produce hundreds of
	 * requests for every created session.
	 *
	 * @param  municipalityId id of the municipality that the facilities belong to
	 * @param  partyId        party id of the customer that owns the facilities
	 * @param  facilities     the facilities to fetch measurement data for
	 * @return                one Data object per category that could be fetched
	 */
	public List<Data> getMeasurementData(String municipalityId, String partyId, List<Facility> facilities) {
		final var facilityIds = ofNullable(facilities).orElse(emptyList())
			.stream()
			.map(Facility::getFacilityId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();

		if (facilityIds.isEmpty()) {
			return emptyList();
		}

		return VALID_CATEGORIES.stream()
			.map(category -> getMeasurementData(municipalityId, partyId, facilityIds, category))
			.filter(Objects::nonNull)
			.toList();
	}

	private Data getMeasurementData(String municipalityId, String partyId, List<String> facilityIds, Category category) {
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

			// Missing measurement data for a category should not fail the whole session — log and skip so the session can
			// still be initialized with the data that could be fetched.
			LOG.warn("Could not fetch measurement data for category '{}' and {} facilities: {}", category, facilityIds.size(), sanitizeForLogging(e.getMessage()));
			return null;
		} catch (final Exception e) {
			LOG.warn("Could not fetch measurement data for category '{}' and {} facilities: {}", category, facilityIds.size(), sanitizeForLogging(e.getMessage()));
			return null;
		}
	}
}
