package se.sundsvall.selfserviceai.integration.measurementdata;

import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.AggregateOnEnum.MONTH;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public class MeasurementDataMapper {

	private MeasurementDataMapper() {}

	public static MeasurementDataSearchParameters toMeasurementDataSearchParameters(String partyId, Agreement agreement) {
		return ofNullable(agreement)
			.map(a -> new MeasurementDataSearchParameters()
				.aggregateOn(MONTH)
				.fromDate(Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant())) // Fetch data for last year
				.toDate(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())) // Fetch data for last year
				.partyId(partyId)
				.category(Objects.isNull(a.getCategory()) ? null : CategoryEnum.fromValue(a.getCategory().toString()))
				.facilityId(a.getFacilityId()))
			.orElse(null);
	}
}
