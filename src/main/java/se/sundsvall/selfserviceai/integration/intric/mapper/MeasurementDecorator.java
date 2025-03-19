package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementPoints;
import generated.se.sundsvall.measurementdata.MeasurementSerie;
import java.util.List;
import java.util.Objects;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.MeasurementData;

public class MeasurementDecorator {
	private MeasurementDecorator() {}

	public static InstalledBase addMeasurements(InstalledBase installedBase, List<Data> measurementDatas) {
		ofNullable(measurementDatas).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(data -> attachToFacility(installedBase, data));

		return installedBase;
	}

	private static void attachToFacility(InstalledBase installedBase, Data data) {
		ofNullable(installedBase.getFacilities()).orElse(emptyList())
			.stream()
			.filter(f -> f.getFacilityId().equals(data.getFacilityId()))
			.findFirst()
			.ifPresent(f -> f.getMeasurements().addAll(toMeasurementDatas(data)));
	}

	private static List<MeasurementData> toMeasurementDatas(Data data) {
		return ofNullable(data.getMeasurementSeries()).orElse(emptyList())
			.stream()
			.map(serie -> toMeasurementData(data, serie))
			.flatMap(List::stream)
			.toList();
	}

	private static List<MeasurementData> toMeasurementData(Data data, MeasurementSerie serie) {
		return ofNullable(serie.getMeasurementPoints()).orElse(emptyList())
			.stream()
			.map(point -> toMeasurementData(data, serie, point))
			.toList();
	}

	private static MeasurementData toMeasurementData(Data data, MeasurementSerie serie, MeasurementPoints measurementPoint) {
		return MeasurementData.builder()
			.withCategory(data.getCategory().name())
			.withMeasurementType(serie.getMeasurementType())
			.withTimestamp(measurementPoint.getTimestamp())
			.withUnit(serie.getUnit())
			.withValue(measurementPoint.getValue())
			.build();
	}

}
