package se.sundsvall.selfserviceai.integration.eneo.mapper;

import generated.se.sundsvall.measurementdata.Category;
import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementPoint;
import generated.se.sundsvall.measurementdata.MeasurementSerie;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.MeasurementData;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class MeasurementDecorator {

	private MeasurementDecorator() {}

	public static void addMeasurements(final List<Facility> facilities, final List<Data> measurementDatas) {
		ofNullable(measurementDatas).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(data -> attachToFacilities(facilities, data));
	}

	private static void attachToFacilities(final List<Facility> facilities, final Data data) {
		ofNullable(data.getMeasurementSeries()).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(measurementSerie -> attachToFacility(facilities, data, measurementSerie));
	}

	private static void attachToFacility(final List<Facility> facilities, final Data data, final MeasurementSerie measurementSerie) {
		final var facilityId = resolveFacilityId(data, measurementSerie);

		if (Objects.isNull(facilityId)) {
			return;
		}

		ofNullable(facilities).orElse(emptyList())
			.stream()
			.filter(facility -> Objects.equals(facility.getFacilityId(), facilityId))
			.findFirst()
			.ifPresent(facility -> facility.getMeasurements().addAll(toMeasurementDatas(data, measurementSerie)));
	}

	/**
	 * Resolves which facility a measurement serie belongs to.
	 *
	 * A serie states its own facility, except when it is an aggregate of the facilities that were requested. An aggregate
	 * of a single requested facility covers exactly that facility and is attributed to it, whereas an aggregate spanning
	 * several facilities belongs to no single one and is therefore left out.
	 *
	 * @param  data             the response the serie was delivered in, holding the requested facility ids
	 * @param  measurementSerie the serie to resolve the facility for
	 * @return                  the id of the facility the serie belongs to, or null if it can not be attributed to one
	 */
	private static String resolveFacilityId(final Data data, final MeasurementSerie measurementSerie) {
		if (nonNull(measurementSerie.getFacilityId())) {
			return measurementSerie.getFacilityId();
		}

		final var requestedFacilityIds = ofNullable(data.getFacilityId()).orElse(emptyList());

		return requestedFacilityIds.size() == 1 ? requestedFacilityIds.getFirst() : null;
	}

	private static List<MeasurementData> toMeasurementDatas(final Data data, final MeasurementSerie measurementSerie) {
		return ofNullable(measurementSerie.getMeasurementPoints()).orElse(emptyList())
			.stream()
			.map(measurementPoint -> toMeasurementData(data, measurementSerie, measurementPoint))
			.toList();
	}

	private static MeasurementData toMeasurementData(final Data data, final MeasurementSerie measurementSerie, final MeasurementPoint measurementPoint) {
		return MeasurementData.builder()
			.withCategory(Optional.ofNullable(data.getCategory()).map(Category::name).orElse(null))
			.withMeasurementType(measurementSerie.getMeasurementType())
			.withTimestamp(measurementPoint.getTimestamp())
			.withUnit(measurementSerie.getUnit())
			.withValue(measurementPoint.getValue())
			.build();
	}
}
