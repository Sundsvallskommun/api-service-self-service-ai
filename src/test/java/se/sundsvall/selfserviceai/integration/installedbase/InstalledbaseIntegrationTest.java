package se.sundsvall.selfserviceai.integration.installedbase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

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
	void getInstalledbase() {
		// Arrange
		final var customer = new InstalledBaseCustomer()
			.addItemsItem(new InstalledBaseItem().facilityId("facilityId1"))
			.addItemsItem(new InstalledBaseItem().facilityId("facilityId2"));

		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID)).thenReturn(new InstalledBaseResponse().addInstalledBaseCustomersItem(customer));

		// Act
		final var result = integration.getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID);
		assertThat(result).isEqualTo(customer);
	}

	@Test
	void getInstalledbaseWhenEmptyResponse() {
		// Arrange
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID)).thenReturn(new InstalledBaseResponse());

		// Act
		final var result = integration.getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID);

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID);
		assertThat(result).isNull();
	}

	@Test
	void getInstalledbaseWhenMultipleMatches() {
		// Arrange
		when(clientMock.getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID)).thenReturn(new InstalledBaseResponse()
			.addInstalledBaseCustomersItem(new InstalledBaseCustomer())
			.addInstalledBaseCustomersItem(new InstalledBaseCustomer()));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> integration.getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID));

		// Assert and verify
		verify(clientMock).getInstalledbase(MUNICIPALITY_ID, CUSTOMER_ENGAGEMENT_ORG_ID, PARTY_ID);
		assertThat(exception.getStatus()).isEqualTo(Status.INTERNAL_SERVER_ERROR);
		assertThat(exception.getMessage()).isEqualTo("Internal Server Error: Installed base response can not be interpreted as it contains more than one match (size is 2)");

	}
}
