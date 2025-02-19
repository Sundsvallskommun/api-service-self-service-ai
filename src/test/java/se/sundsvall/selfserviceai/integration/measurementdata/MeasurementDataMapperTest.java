package se.sundsvall.selfserviceai.integration.measurementdata;

import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.AggregateOnEnum.MONTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.agreement.Category;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class MeasurementDataMapperTest {

	@ParameterizedTest
	@EnumSource(value = Category.class, names = {
		"COMMUNICATION", "DISTRICT_HEATING", "ELECTRICITY", "WASTE_MANAGEMENT"
	})
	void toMeasurementDataSearchParameters(Category category) {

		// Arrange
		final var partyId = "partyId";
		final var facilityId = "facilityId";
		final var agreement = new Agreement()
			.category(category)
			.facilityId(facilityId);

		// Act
		final var result = MeasurementDataMapper.toMeasurementDataSearchParameters(partyId, agreement);

		// Assert
		assertThat(result.getAggregateOn()).isEqualTo(MONTH);
		assertThat(result.getCategory().name()).isEqualTo(category.name());
		assertThat(result.getFacilityId()).isEqualTo(facilityId);
		assertThat(result.getFromDate()).isEqualTo(Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		assertThat(result.getToDate()).isEqualTo(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		assertThat(result.getPartyId()).isEqualTo(partyId);
	}

	@ParameterizedTest
	@EnumSource(value = Category.class, names = {
		"COMMUNICATION", "DISTRICT_HEATING", "ELECTRICITY", "WASTE_MANAGEMENT"
	}, mode = EXCLUDE)
	void toMeasurementDataSearchParametersWithInvalidCategory(Category category) {

		// Arrange
		final var partyId = "partyId";
		final var facilityId = "facilityId";
		final var agreement = new Agreement()
			.category(category)
			.facilityId(facilityId);

		// Act
		final var exception = assertThrows(Exception.class, () -> MeasurementDataMapper.toMeasurementDataSearchParameters(partyId, agreement));

		// Assert
		assertThat(exception.getMessage()).isEqualTo("Unexpected value '%s'".formatted(category.name()));
	}

	@Test
	void toMeasurementDataSearchParametersFromNullValues() {
		// Act
		final var result = MeasurementDataMapper.toMeasurementDataSearchParameters(null, new Agreement());

		// Assert
		assertThat(result.getAggregateOn()).isEqualTo(MONTH);
		assertThat(result.getCategory()).isNull();
		assertThat(result.getFacilityId()).isNull();
		assertThat(result.getFromDate()).isEqualTo(Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		assertThat(result.getToDate()).isEqualTo(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		assertThat(result.getPartyId()).isNull();
	}

	@Test
	void toMeasurementDataSearchParametersFromNullParams() {
		assertThat(MeasurementDataMapper.toMeasurementDataSearchParameters(null, null)).isNull();
	}

}
