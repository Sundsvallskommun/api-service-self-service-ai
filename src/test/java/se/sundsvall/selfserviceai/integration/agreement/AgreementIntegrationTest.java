package se.sundsvall.selfserviceai.integration.agreement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_GATEWAY;
import static org.zalando.problem.Status.NOT_FOUND;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.agreement.AgreementParty;
import generated.se.sundsvall.agreement.AgreementResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

@ExtendWith(MockitoExtension.class)
class AgreementIntegrationTest {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String PARTY_ID = "partyId";

	@Mock
	private AgreementClient clientMock;

	@InjectMocks
	private AgreementIntegration integration;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getAgreements() {
		// Arrange
		final var agreement1 = new Agreement();
		final var agreement2 = new Agreement();

		when(clientMock.getAgreements(MUNICIPALITY_ID, PARTY_ID, true)).thenReturn(new AgreementResponse()
			.addAgreementPartiesItem(new AgreementParty()
				.addAgreementsItem(agreement1))
			.addAgreementPartiesItem(new AgreementParty()
				.addAgreementsItem(agreement2)));

		// Act
		final var result = integration.getAgreements(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		assertThat(result).containsExactlyInAnyOrder(agreement1, agreement2);
		verify(clientMock).getAgreements(MUNICIPALITY_ID, PARTY_ID, true);
	}

	@Test
	void getAgreementsWhenResponseContainsNullList() {
		// Arrange
		when(clientMock.getAgreements(MUNICIPALITY_ID, PARTY_ID, true)).thenReturn(new AgreementResponse());

		// Act
		final var result = integration.getAgreements(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		assertThat(result).isEmpty();
		verify(clientMock).getAgreements(MUNICIPALITY_ID, PARTY_ID, true);
	}

	@Test
	void getAgreementsThrows404Exception() {
		// Arrange
		when(clientMock.getAgreements(MUNICIPALITY_ID, PARTY_ID, true)).thenThrow(Problem.valueOf(NOT_FOUND));

		// Act
		final var result = integration.getAgreements(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		assertThat(result).isEmpty();
		verify(clientMock).getAgreements(MUNICIPALITY_ID, PARTY_ID, true);
	}

	@Test
	void getAgreementsThrowsException() {
		// Arrange
		when(clientMock.getAgreements(MUNICIPALITY_ID, PARTY_ID, true)).thenThrow(Problem.valueOf(BAD_GATEWAY, "Bad to the bone"));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> integration.getAgreements(MUNICIPALITY_ID, PARTY_ID));

		// Assert and verify
		assertThat(exception.getStatus()).isEqualTo(BAD_GATEWAY);
		assertThat(exception.getMessage()).isEqualTo("Bad Gateway: Bad to the bone");
		verify(clientMock).getAgreements(MUNICIPALITY_ID, PARTY_ID, true);
	}
}
