package se.sundsvall.selfserviceai.integration.measurementdata;

import static generated.se.sundsvall.measurementdata.Category.DISTRICT_HEATING;
import static generated.se.sundsvall.measurementdata.Category.ELECTRICITY;
import static generated.se.sundsvall.measurementdata.MeasurementDataSearchParameters.AggregateOnEnum.MONTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.NullSource;
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
	void getMeasurementDataWithProcessableCategories() {

		// Arrange
		final var facilityId1 = "facilityId1";
		final var facilityId2 = "facilityId2";
		final var fromDate = Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final var toDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final var agreement1 = new Agreement().facilityId(facilityId1).category(Category.DISTRICT_HEATING);
		final var agreement2 = new Agreement().facilityId(facilityId2).category(Category.ELECTRICITY);
		final var data1 = new Data().category(DISTRICT_HEATING);
		final var data2 = new Data().category(ELECTRICITY);

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1)).thenReturn(data1);
		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId2)).thenReturn(data2);

		// Act
		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, List.of(agreement1, agreement2));

		// Assert and verify
		assertThat(result).containsExactlyInAnyOrder(data1, data2);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId2);
	}

	@ParameterizedTest
	@EnumSource(value = Category.class, mode = Mode.EXCLUDE, names = {
		"DISTRICT_HEATING", "ELECTRICITY", "COMMUNICATION", "WASTE_MANAGEMENT"
	})
	@NullSource
	void getMeasurementDataWithNonProcessableCategory(Category category) {
		final var agreement = new Agreement().category(category);

		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, List.of(agreement));

		// Assert and verify
		assertThat(result).isEmpty();
	}

	@Test
	void getMeasurementDataWithMixedProcessableCategoriesWhereSomeNotImplementedInMeasurementData() {

		// Arrange
		final var facilityId1 = "facilityId1";
		final var facilityId2 = "facilityId2";
		final var facilityId3 = "facilityId3";
		final var facilityId4 = "facilityId4";
		final var fromDate = Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final var toDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final var agreement1 = new Agreement().facilityId(facilityId1).category(Category.DISTRICT_HEATING);
		final var agreement2 = new Agreement().facilityId(facilityId2).category(Category.ELECTRICITY);
		final var agreement3 = new Agreement().facilityId(facilityId3).category(Category.COMMUNICATION);
		final var agreement4 = new Agreement().facilityId(facilityId4).category(Category.WASTE_MANAGEMENT);
		final var data1 = new Data().category(DISTRICT_HEATING);
		final var data2 = new Data().category(ELECTRICITY);

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1)).thenReturn(data1);
		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId2)).thenReturn(data2);
		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.COMMUNICATION, facilityId3)).thenThrow(Problem.valueOf(NOT_IMPLEMENTED));
		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.WASTE_MANAGEMENT, facilityId4)).thenThrow(Problem.valueOf(NOT_IMPLEMENTED));

		// Act
		final var result = integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, List.of(agreement1, agreement2, agreement3, agreement4));

		// Assert and verify
		assertThat(result).containsExactlyInAnyOrder(data1, data2);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.ELECTRICITY, facilityId2);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.COMMUNICATION, facilityId3);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.WASTE_MANAGEMENT, facilityId4);
	}

	@Test
	void getMeasurementDataWhenCategoryThrowsException() {

		// Arrange
		final var facilityId1 = "facilityId1";
		final var fromDate = Date.from(LocalDate.now().minusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final var toDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final var agreements = List.of(new Agreement().facilityId(facilityId1).category(Category.DISTRICT_HEATING));

		when(clientMock.getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1)).thenThrow(Problem.valueOf(BAD_GATEWAY, "Bad to the bone"));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> integration.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, agreements));

		// Assert and verify
		assertThat(exception.getStatus()).isEqualTo(BAD_GATEWAY);
		verify(clientMock).getMeasurementData(MUNICIPALITY_ID, MONTH, fromDate, toDate, PARTY_ID, CategoryEnum.DISTRICT_HEATING, facilityId1);
	}
}
