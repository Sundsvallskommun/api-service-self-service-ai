package se.sundsvall.selfserviceai.integration.measurementdata;

import generated.se.sundsvall.measurementdata.Data;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;

import static generated.se.sundsvall.measurementdata.Aggregation.MONTH;
import static generated.se.sundsvall.measurementdata.Category.DISTRICT_HEATING;
import static generated.se.sundsvall.measurementdata.Category.ELECTRICITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ExtendWith(MockitoExtension.class)
class MeasurementDataIntegrationTest {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String PARTY_ID = "partyId";
	private static final String FACILITY_ID_1 = "facilityId1";
	private static final String FACILITY_ID_2 = "facilityId2";
	private static final String FROM_DATE = LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
	private static final String TO_DATE = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME);

	@Mock
	private MeasurementDataClient clientMock;

	@InjectMocks
	private MeasurementDataIntegration integration;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void categoriesAreTheImplementedOnes() {
		assertThat(MeasurementDataIntegration.CATEGORIES).containsExactly(DISTRICT_HEATING, ELECTRICITY);
	}

	@Test
	void getMeasurementDataSendsAllFacilitiesInOneRequest() {

		// Arrange
		final var facilityIds = List.of(FACILITY_ID_1, FACILITY_ID_2);
		final var facilities = List.of(
			Facility.builder().withFacilityId(FACILITY_ID_1).build(),
			Facility.builder().withFacilityId(FACILITY_ID_2).build());
		final var electricityData = new Data().category(ELECTRICITY).facilityId(facilityIds);

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, ELECTRICITY, facilityIds)).thenReturn(electricityData);

		// Act
		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, facilities, ELECTRICITY);

		// Assert and verify
		assertThat(result).isSameAs(electricityData);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, ELECTRICITY, facilityIds);
	}

	@Test
	void getMeasurementDataForDuplicateAndNullFacilityIds() {

		// Arrange
		final var facilities = List.of(
			Facility.builder().withFacilityId(FACILITY_ID_1).build(),
			Facility.builder().withFacilityId(FACILITY_ID_1).build(),
			Facility.builder().build());

		// Act
		integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, facilities, DISTRICT_HEATING);

		// Assert and verify
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, DISTRICT_HEATING, List.of(FACILITY_ID_1));
	}

	@Test
	void getMeasurementDataWhenNoFacilitiesToFetchDataFor() {

		// Act
		final var fromNull = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, null, ELECTRICITY);
		final var fromEmptyList = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, List.of(), ELECTRICITY);
		final var fromFacilityWithoutId = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, List.of(Facility.builder().build()), ELECTRICITY);

		// Assert and verify
		assertThat(fromNull).isNull();
		assertThat(fromEmptyList).isNull();
		assertThat(fromFacilityWithoutId).isNull();
		verifyNoInteractions(clientMock);
	}

	@Test
	void getMeasurementDataWhenServiceThrowsNotImplementedException() {

		// Arrange — a category that the backend does not implement is "no data", not a failure
		final var facilityIds = List.of(FACILITY_ID_1);
		final var facilities = List.of(Facility.builder().withFacilityId(FACILITY_ID_1).build());
		final var exception = Problem.valueOf(BAD_GATEWAY, "datawarehousereader error: {detail=aggregation 'MONTH' and category 'DISTRICT_HEATING', status=501 Not Implemented, title=Not Implemented}");

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, DISTRICT_HEATING, facilityIds)).thenThrow(exception);

		// Act
		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, facilities, DISTRICT_HEATING);

		// Assert and verify
		assertThat(result).isNull();
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, DISTRICT_HEATING, facilityIds);
	}

	@Test
	void getMeasurementDataWhenServiceThrowsOtherProblem() {

		// Arrange — any other failure is propagated so that the caller can record the category as missing
		final var facilityIds = List.of(FACILITY_ID_1);
		final var facilities = List.of(Facility.builder().withFacilityId(FACILITY_ID_1).build());
		final var exception = Problem.valueOf(BAD_GATEWAY, "Bad to the bone");

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, DISTRICT_HEATING, facilityIds)).thenThrow(exception);

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, facilities, DISTRICT_HEATING));

		// Assert and verify
		assertThat(e).isSameAs(exception);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, DISTRICT_HEATING, facilityIds);
	}

	@Test
	void getMeasurementDataWhenServiceThrowsGenericException() {

		// Arrange
		final var facilityIds = List.of(FACILITY_ID_1);
		final var facilities = List.of(Facility.builder().withFacilityId(FACILITY_ID_1).build());
		final var exception = new RuntimeException("boom");

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, ELECTRICITY, facilityIds)).thenThrow(exception);

		// Act
		final var e = assertThrows(RuntimeException.class, () -> integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, facilities, ELECTRICITY));

		// Assert and verify
		assertThat(e).isSameAs(exception);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, FROM_DATE, TO_DATE, PARTY_ID, ELECTRICITY, facilityIds);
	}
}
