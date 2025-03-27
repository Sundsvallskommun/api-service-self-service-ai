package se.sundsvall.selfserviceai.integration.measurementdata;

import static generated.se.sundsvall.measurementdata.Category.DISTRICT_HEATING;
import static generated.se.sundsvall.measurementdata.Category.ELECTRICITY;
import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.AggregateOnEnum.MONTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_GATEWAY;

import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;

@ExtendWith(MockitoExtension.class)
class MeasurementDataIntegrationTest {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String PARTY_ID = "partyId";

	@Mock
	private MeasurementDataClient clientMock;

	@InjectMocks
	private MeasurementDataIntegration integration;

	@Captor
	private ArgumentCaptor<MeasurementDataSearchParameters> parameterCaptor;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getMeasurementDataWithProcessableCategories() {

		// Arrange
		final var facilityId1 = "facilityId1";
		final var facilityId2 = "facilityId2";
		final var fromDate = LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
		final var toDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
		final var facility1 = Facility.builder().withFacilityId(facilityId1).build();
		final var facility2 = Facility.builder().withFacilityId(facilityId2).build();
		final var data1 = new Data().category(DISTRICT_HEATING).facilityId(facilityId1);
		final var data2 = new Data().category(ELECTRICITY).facilityId(facilityId1);
		final var data3 = new Data().category(DISTRICT_HEATING).facilityId(facilityId2);
		final var data4 = new Data().category(ELECTRICITY).facilityId(facilityId2);

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1)).thenReturn(data1);
		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId1)).thenReturn(data2);
		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId2)).thenReturn(data3);
		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId2)).thenReturn(data4);

		// Act
		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, List.of(facility1, facility2));

		// Assert and verify
		assertThat(result).containsExactlyInAnyOrder(data1, data2, data3, data4);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId2);
	}

	@Test
	void getMeasurementDataWhenServiceThrowsNotImplementedException() {

		// Arrange
		final var facilityId1 = "facilityId1";
		final var data = new Data().category(ELECTRICITY).facilityId(facilityId1);
		final var fromDate = LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
		final var toDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
		final var facilities = List.of(Facility.builder().withFacilityId(facilityId1).build());
		final var exception = Problem.valueOf(BAD_GATEWAY, "datawarehousereader error: {detail=aggregation 'MONTH' and category 'DISTRICT_HEATING', status=501 Not Implemented, title=Not Implemented}");

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1)).thenThrow(exception);
		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId1)).thenReturn(data);

		// Act
		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, facilities);

		// Assert and verify
		assertThat(result).containsExactly(data);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId1);
	}

	@Test
	void getMeasurementDataWhenServiceThrowsUnhandledException() {

		// Arrange
		final var facilityId1 = "facilityId1";
		final var fromDate = LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
		final var toDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
		final var facilities = List.of(Facility.builder().withFacilityId(facilityId1).build());
		final var exception = Problem.valueOf(BAD_GATEWAY, "Bad to the bone");

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1)).thenThrow(exception);

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, facilities));

		// Assert and verify
		assertThat(e).isSameAs(exception);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1);
		verify(clientMock, atMostOnce()).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId1);
	}
}
