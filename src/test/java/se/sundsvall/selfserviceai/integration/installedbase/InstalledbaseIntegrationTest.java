package se.sundsvall.selfserviceai.integration.installedbase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstalledbaseIntegrationTest {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String CUSTOMER_ENGAGEMENT_ORG_ID = "customerEngagementOrgId";
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
	void getInstalledbaseWhenEmptyResponse() {
		// Arrange
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID)).thenReturn(new InstalledBaseResponse());

		// Act
		final var result = integration.getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID);
		assertThat(result).isEmpty();
	}

	@Test
	void getInstalledbase() {
		// Arrange
		final var facility1 = new InstalledBaseItem().facilityId("facilityId1");
		final var facility2 = new InstalledBaseItem().facilityId("facilityId2");
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID)).thenReturn(new InstalledBaseResponse()
			.addInstalledBaseCustomersItem(
				new InstalledBaseCustomer()
					.addItemsItem(facility1)
					.addItemsItem(facility2)));

		// Act
		final var result = integration.getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID);
		assertThat(result).containsExactlyInAnyOrder(facility1, facility2);
	}
}
