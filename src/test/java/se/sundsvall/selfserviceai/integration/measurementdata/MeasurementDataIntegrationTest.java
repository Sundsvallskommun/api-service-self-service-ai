package se.sundsvall.selfserviceai.integration.measurementdata;

import static generated.se.sundsvall.measurementdata.Category.DISTRICT_HEATING;
import static generated.se.sundsvall.measurementdata.Category.ELECTRICITY;
import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.AggregateOnEnum.MONTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_GATEWAY;
import static org.zalando.problem.Status.NOT_IMPLEMENTED;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.agreement.Category;
import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters;
import generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.CategoryEnum;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
	void getMeasurementDataWhenCategoryMatches() {

		// Arrange
		final var agreement1 = new Agreement().facilityId("facilityId1").category(Category.DISTRICT_HEATING);
		final var agreement2 = new Agreement().facilityId("facilityId2").category(Category.ELECTRICITY);
		final var data1 = new Data().category(DISTRICT_HEATING);
		final var data2 = new Data().category(ELECTRICITY);

		when(clientMock.getMeasurementData(eq(MUNICIPALITY_ID), argThat(arg -> arg.getCategory() == CategoryEnum.DISTRICT_HEATING))).thenReturn(data1);
		when(clientMock.getMeasurementData(eq(MUNICIPALITY_ID), argThat(arg -> arg.getCategory() == CategoryEnum.ELECTRICITY))).thenReturn(data2);

		// Act
		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, List.of(agreement1, agreement2));

		// Assert and verify
		verify(clientMock, times(2)).getMeasurementData(eq(MUNICIPALITY_ID), parameterCaptor.capture());
		assertThat(result).containsExactlyInAnyOrder(data1, data2);

		// Assert request for each call
		assertRequest(0, CategoryEnum.DISTRICT_HEATING, "facilityId1");
		assertRequest(1, CategoryEnum.ELECTRICITY, "facilityId2");
	}

	@Test
	void getMeasurementDataWhenCategoryNotImplemented() {

		// Arrange
		final var agreement1 = new Agreement().facilityId("facilityId1").category(Category.DISTRICT_HEATING);
		final var agreement2 = new Agreement().facilityId("facilityId2").category(Category.ELECTRICITY);
		final var agreement3 = new Agreement().facilityId("facilityId3").category(Category.COMMUNICATION);
		final var agreement4 = new Agreement().facilityId("facilityId4").category(Category.WASTE_MANAGEMENT);
		final var data1 = new Data().category(DISTRICT_HEATING);
		final var data2 = new Data().category(ELECTRICITY);

		when(clientMock.getMeasurementData(eq(MUNICIPALITY_ID), argThat(arg -> arg.getCategory() == CategoryEnum.DISTRICT_HEATING))).thenReturn(data1);
		when(clientMock.getMeasurementData(eq(MUNICIPALITY_ID), argThat(arg -> arg.getCategory() == CategoryEnum.ELECTRICITY))).thenReturn(data2);
		when(clientMock.getMeasurementData(eq(MUNICIPALITY_ID), argThat(arg -> arg.getCategory() == CategoryEnum.COMMUNICATION))).thenThrow(Problem.valueOf(NOT_IMPLEMENTED));
		when(clientMock.getMeasurementData(eq(MUNICIPALITY_ID), argThat(arg -> arg.getCategory() == CategoryEnum.WASTE_MANAGEMENT))).thenThrow(Problem.valueOf(NOT_IMPLEMENTED));

		// Act
		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, List.of(agreement1, agreement2, agreement3, agreement4));

		// Assert and verify
		verify(clientMock, times(4)).getMeasurementData(eq(MUNICIPALITY_ID), parameterCaptor.capture());
		assertThat(result).containsExactlyInAnyOrder(data1, data2);

		// Assert request for each call
		assertRequest(0, CategoryEnum.DISTRICT_HEATING, "facilityId1");
		assertRequest(1, CategoryEnum.ELECTRICITY, "facilityId2");
		assertRequest(2, CategoryEnum.COMMUNICATION, "facilityId3");
		assertRequest(3, CategoryEnum.WASTE_MANAGEMENT, "facilityId4");
	}

	@Test
	void getMeasurementDataWhenCategoryThrowsException() {

		// Arrange
		final var agreements = List.of(new Agreement().facilityId("facilityId1").category(Category.DISTRICT_HEATING));

		when(clientMock.getMeasurementData(eq(MUNICIPALITY_ID), any(MeasurementDataSearchParameters.class))).thenThrow(Problem.valueOf(BAD_GATEWAY, "Bad to the bone"));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, agreements));

		verify(clientMock).getMeasurementData(eq(MUNICIPALITY_ID), any(MeasurementDataSearchParameters.class));
		assertThat(exception.getStatus()).isEqualTo(BAD_GATEWAY);
	}

	private void assertRequest(int position, CategoryEnum expectedCategory, String exepectedFacilityId) {
		assertThat(parameterCaptor.getAllValues().get(position).getAggregateOn()).isEqualTo(MONTH);
		assertThat(parameterCaptor.getAllValues().get(position).getCategory()).isEqualTo(expectedCategory);
		assertThat(parameterCaptor.getAllValues().get(position).getFacilityId()).isEqualTo(exepectedFacilityId);
		assertThat(parameterCaptor.getAllValues().get(position).getFromDate()).isEqualTo(Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		assertThat(parameterCaptor.getAllValues().get(position).getToDate()).isEqualTo(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		assertThat(parameterCaptor.getAllValues().get(position).getPartyId()).isEqualTo(PARTY_ID);
	}
}
