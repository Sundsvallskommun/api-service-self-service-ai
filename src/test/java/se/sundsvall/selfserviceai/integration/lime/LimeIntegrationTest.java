package se.sundsvall.selfserviceai.integration.lime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import generated.se.sundsvall.invoices.InvoicesResponse;
import generated.se.sundsvall.invoices.MetaData;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.selfserviceai.integration.intric.model.SessionPublic;

@ExtendWith(MockitoExtension.class)
class LimeIntegrationTest {

	private static final String PARTY_ID = "partyId";
	private static final String CUSTOMER_NUMBER = "121212";
	private static final String SESSION_ID = "sessionId";

	@Mock
	private LimeClient clientMock;

	@Mock
	private InvoicesResponse responseMock;

	@Mock
	private MetaData metaDataMock;

	@InjectMocks
	private LimeIntegration integration;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void saveHistory() {

		// Arrange and act
		integration.saveChatHistory(PARTY_ID, CUSTOMER_NUMBER, SessionPublic.builder().build());

		// Assert and verify
		verify(clientMock).saveChatHistory(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class));
	}

	@Test
	void saveHistoryThrowsException() {

		// Arrange
		final var session = SessionPublic.builder().build();

		when(clientMock.saveChatHistory(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class)))
			.thenThrow(Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout"));

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> integration.saveChatHistory(PARTY_ID, CUSTOMER_NUMBER, session));

		// Assert and verify
		verify(clientMock).saveChatHistory(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class));
		assertThat(e.getStatus()).isEqualTo(Status.I_AM_A_TEAPOT);
		assertThat(e.getMessage()).isEqualTo("I'm a teapot: Big and stout");
	}

	@Test
	void getHistory() {

		// Arrange
		final var response = new ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse();
		when(clientMock.getChatHistory(SESSION_ID)).thenReturn(response);

		// Act
		final var result = integration.getChatHistory(SESSION_ID);

		// Assert and verify
		verify(clientMock).getChatHistory(SESSION_ID);
		assertThat(result).isSameAs(response);
	}

	@Test
	void getHistoryThrowsException() {

		// Arrange
		when(clientMock.getChatHistory(SESSION_ID)).thenThrow(Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout"));

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> integration.getChatHistory(SESSION_ID));

		// Assert and verify
		verify(clientMock).getChatHistory(SESSION_ID);
		assertThat(e.getStatus()).isEqualTo(Status.I_AM_A_TEAPOT);
		assertThat(e.getMessage()).isEqualTo("I'm a teapot: Big and stout");
	}

}
