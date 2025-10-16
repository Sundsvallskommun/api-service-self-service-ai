package se.sundsvall.selfserviceai.integration.eneo.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementPoints;
import generated.se.sundsvall.measurementdata.MeasurementSerie;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.MeasurementData;

public class MeasurementDecorator {

	private MeasurementDecorator() {}

	public static void addMeasurements(final List<Facility> facilities, final List<Data> measurementDatas) {
		ofNullable(measurementDatas).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(data -> attachToFacility(facilities, data));
	}

	private static void attachToFacility(final List<Facility> facilities, final Data data) {
		ofNullable(facilities).orElse(emptyList())
			.stream()
			.filter(facility -> facility.getFacilityId().equals(data.getFacilityId()))
			.findFirst()
			.ifPresent(facility -> facility.getMeasurements().addAll(toMeasurementDatas(data)));
	}

	private static List<MeasurementData> toMeasurementDatas(final Data data) {
		return ofNullable(data.getMeasurementSeries()).orElse(emptyList())
			.stream()
			.map(measurementSerie -> toMeasurementData(data, measurementSerie))
			.flatMap(List::stream)
			.toList();
	}

	private static List<MeasurementData> toMeasurementData(final Data data, final MeasurementSerie measurementSerie) {
		return ofNullable(measurementSerie.getMeasurementPoints()).orElse(emptyList())
			.stream()
			.map(point -> toMeasurementData(data, measurementSerie, point))
			.toList();
	}

	private static MeasurementData toMeasurementData(final Data data, final MeasurementSerie measurementSerie, final MeasurementPoints measurementPoint) {
		return MeasurementData.builder()
			.withCategory(Optional.ofNullable(data.getCategory()).map(Objects::toString).orElse(null))
			.withMeasurementType(measurementSerie.getMeasurementType())
			.withTimestamp(measurementPoint.getTimestamp())
			.withUnit(measurementSerie.getUnit())
			.withValue(measurementPoint.getValue())
			.build();
	}
}
