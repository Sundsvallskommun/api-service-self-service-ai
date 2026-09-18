package se.sundsvall.selfserviceai.integration.installedbase;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseResponse;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class InstalledbaseIntegrationTest {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String CUSTOMER_ENGAGEMENT_ORG_ID1 = "customerEngagementOrgId1";
	private static final String CUSTOMER_ENGAGEMENT_ORG_ID2 = "customerEngagementOrgId2";
	private static final Set<String> CUSTOMER_ENGAGEMENT_ORG_IDS = Set.of(CUSTOMER_ENGAGEMENT_ORG_ID1, CUSTOMER_ENGAGEMENT_ORG_ID2);
	private static final String CUSTOMER_NBR = "12345";
	private static final String PARTY_ID = "partyId";

	@Mock
	private InstalledbaseClient clientMock;

	@InjectMocks
	private InstalledbaseIntegration integration;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getInstalledbases() {
		// Arrange
		final var ib1 = new InstalledBaseCustomer()
			.customerNumber(CUSTOMER_NBR)
			.partyId(PARTY_ID)
			.addItemsItem(new InstalledBaseItem().type("TYPE1").facilityId("facilityId1"))
			.addItemsItem(new InstalledBaseItem().type("TYPE1").facilityId("facilityId2"));
		final var ib2 = new InstalledBaseCustomer()
			.customerNumber(CUSTOMER_NBR)
			.partyId(PARTY_ID)
			.addItemsItem(new InstalledBaseItem().type("TYPE2").facilityId("facilityId1"))
			.addItemsItem(new InstalledBaseItem().type("TYPE2").facilityId("facilityId4"));

		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID)).thenReturn(new InstalledBaseResponse().addInstalledBaseCustomersItem(ib1));
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID)).thenReturn(new InstalledBaseResponse().addInstalledBaseCustomersItem(ib2));

		// Act
		final var result = integration.getInstalledbases(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_IDS);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID);
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID);
		assertThat(result).isNotNull().hasSize(2)
			.containsEntry(CUSTOMER_ENGAGEMENT_ORG_ID1, ib1)
			.containsEntry(CUSTOMER_ENGAGEMENT_ORG_ID2, ib2);
	}

	@Test
	void getInstalledbasesWhenEmptyResponse() {
		// Arrange
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID)).thenReturn(new InstalledBaseResponse());
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID)).thenReturn(new InstalledBaseResponse());

		// Act
		final var result = integration.getInstalledbases(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_IDS);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID);
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID);
		assertThat(result).isEmpty();
	}

	@Test
	void getInstalledbaseWhenMultipleMatches() {
		// Arrange
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID)).thenReturn(new InstalledBaseResponse()
			.addInstalledBaseCustomersItem(new InstalledBaseCustomer())
			.addInstalledBaseCustomersItem(new InstalledBaseCustomer()));

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> integration.getInstalledbases(MUNICIPALITY_ID, PARTY_ID, Set.of(CUSTOMER_ENGAGEMENT_ORG_ID1)));

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID);
		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualTo("Internal Server Error: Installed base response can not be interpreted as it contains more than one match (size is 2)");
	}

	@Test
	void getInstalledbaseWhenCounterpartAnswersNotFound() {
		// Arrange — a 404 is how installedbase answers for a counterpart the customer has no engagement with, so that
		// counterpart is skipped while the others are still fetched
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID)).thenThrow(Problem.valueOf(NOT_FOUND, "No customer engagements matched the search criteria!"));
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID)).thenReturn(new InstalledBaseResponse()
			.installedBaseCustomers(List.of(new InstalledBaseCustomer().customerNumber(CUSTOMER_NBR))));

		// Act
		final var result = integration.getInstalledbases(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_IDS);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID);
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID);
		assertThat(result).containsOnlyKeys(CUSTOMER_ENGAGEMENT_ORG_ID2);
		assertThat(result.get(CUSTOMER_ENGAGEMENT_ORG_ID2).getCustomerNumber()).isEqualTo(CUSTOMER_NBR);
	}

	@Test
	void getInstalledbasePropagatesException() {
		// Arrange — a failure must not be mistaken for a customer without installed base, so it is propagated
		final var exception = Problem.valueOf(BAD_GATEWAY, "Bad to the bone");

		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID)).thenThrow(exception);

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> integration.getInstalledbases(MUNICIPALITY_ID, PARTY_ID, Set.of(CUSTOMER_ENGAGEMENT_ORG_ID1)));

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID);
		assertThat(e).isSameAs(exception);
	}
}
