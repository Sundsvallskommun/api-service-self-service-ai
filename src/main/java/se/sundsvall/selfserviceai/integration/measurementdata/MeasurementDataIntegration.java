package se.sundsvall.selfserviceai.integration.measurementdata;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.NOT_IMPLEMENTED;
import static se.sundsvall.selfserviceai.integration.measurementdata.MeasurementDataMapper.toMeasurementDataSearchParameters;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum;
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
			.map(agreement -> toMeasurementDataSearchParameters(partyId, agreement))
			.map(searchParameters -> getMeasurementData(municipalityId, searchParameters))
			.filter(Objects::nonNull) // null is returned if category is not implemented in measurement data service and must be filtered out from response
			.toList();
	}

	private Data getMeasurementData(String municipalityId, MeasurementDataSearchParameters searchParameters) {
		try {
			return measurementDataClient.getMeasurementData(municipalityId, searchParameters);
		} catch (final ThrowableProblem e) {
			if (Objects.equals(NOT_IMPLEMENTED, e.getStatus())) {
				return null;
			}

			throw e;
		}
	}

	private static boolean hasProcessableCategory(Agreement agreement) {
		return List.of(CategoryEnum.values()).stream()
			.anyMatch(member -> agreement.getCategory().name().equals(member.name()));
	}
}
