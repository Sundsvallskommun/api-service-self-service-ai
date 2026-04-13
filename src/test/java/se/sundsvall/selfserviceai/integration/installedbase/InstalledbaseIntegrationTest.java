package se.sundsvall.selfserviceai.integration.installedbase;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseResponse;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

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
	void getInstalledbaseWhenMultipleMatchesIsSwallowed() {
		// Arrange — a counterpart with multiple matches is now skipped, the other is still returned
		final var ib = new InstalledBaseCustomer().customerNumber(CUSTOMER_NBR).partyId(PARTY_ID);

		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID)).thenReturn(new InstalledBaseResponse()
			.addInstalledBaseCustomersItem(new InstalledBaseCustomer())
			.addInstalledBaseCustomersItem(new InstalledBaseCustomer()));
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID)).thenReturn(new InstalledBaseResponse().addInstalledBaseCustomersItem(ib));

		// Act
		final var result = integration.getInstalledbases(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_IDS);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID);
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID);
		assertThat(result).containsOnly(java.util.Map.entry(CUSTOMER_ENGAGEMENT_ORG_ID2, ib));
	}

	@Test
	void getInstalledbaseSwallowsExceptionPerCounterpart() {
		// Arrange — a transport failure for one counterpart must not block the other
		final var ib = new InstalledBaseCustomer().customerNumber(CUSTOMER_NBR).partyId(PARTY_ID);

		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID)).thenThrow(Problem.valueOf(BAD_GATEWAY, "Bad to the bone"));
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID)).thenReturn(new InstalledBaseResponse().addInstalledBaseCustomersItem(ib));

		// Act
		final var result = integration.getInstalledbases(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_IDS);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID);
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID);
		assertThat(result).containsOnly(java.util.Map.entry(CUSTOMER_ENGAGEMENT_ORG_ID2, ib));
	}

	@Test
	void getInstalledbaseSwallowsExceptionForAllCounterparts() {
		// Arrange — when all counterparts fail the result is simply empty (and the caller skips enrichment)
		when(clientMock.getInstalledbase(eq(MUNICIPALITY_ID), any(), eq(PARTY_ID))).thenThrow(Problem.valueOf(BAD_GATEWAY, "Bad to the bone"));

		// Act
		final var result = integration.getInstalledbases(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_IDS);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID1, PARTY_ID);
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID2, PARTY_ID);
		assertThat(result).isEmpty();
	}

}
