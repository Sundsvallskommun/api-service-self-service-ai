package se.sundsvall.selfserviceai.integration.lime;

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
import se.sundsvall.selfserviceai.integration.db.HistoryRepository;
import se.sundsvall.selfserviceai.integration.db.model.HistoryEntity;
import se.sundsvall.selfserviceai.integration.eneo.model.SessionPublic;
import se.sundsvall.selfserviceai.service.util.JsonBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimeIntegrationTest {

	private static final String PARTY_ID = "partyId";
	private static final String CUSTOMER_NUMBER = "121212";
	private static final String SESSION_ID = "sessionId";

	@Mock
	private HistoryRepository historyRepositoryMock;

	@Mock
	private JsonBuilder jsonBuilderMock;

	@Mock
	private LimeClient limeClientMock;

	@Mock
	private InvoicesResponse invoiceResponseMock;

	@Mock
	private MetaData metaDataMock;

	@InjectMocks
	private LimeIntegration integration;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(historyRepositoryMock, jsonBuilderMock, limeClientMock);
	}

	@Test
	void saveHistory() {

		// Arrange and act
		integration.saveChatHistory(PARTY_ID, CUSTOMER_NUMBER, SessionPublic.builder().build());

		// Assert and verify
		verify(limeClientMock).saveChatHistory(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class));
	}

	@Test
	void saveHistoryLimeThrowsException() {

		// Arrange
		final var session = SessionPublic.builder().build();

		when(limeClientMock.saveChatHistory(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class)))
			.thenThrow(Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout"));

		// Act
		integration.saveChatHistory(PARTY_ID, CUSTOMER_NUMBER, session);

		// Assert and verify
		verify(limeClientMock).saveChatHistory(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class));
		verify(jsonBuilderMock).toJsonString(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class));
		verify(historyRepositoryMock).save(any(HistoryEntity.class));
	}

	@Test
	void saveHistoryLocalRepositoryThrowsException() {

		// Arrange
		final var session = SessionPublic.builder().build();

		when(limeClientMock.saveChatHistory(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class)))
			.thenThrow(Problem.valueOf(Status.FORBIDDEN, "You shall not pass"));
		when(historyRepositoryMock.save(any()))
			.thenThrow(Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout"));

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> integration.saveChatHistory(PARTY_ID, CUSTOMER_NUMBER, session));

		// Assert and verify
		verify(limeClientMock).saveChatHistory(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class));
		verify(jsonBuilderMock).toJsonString(any(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest.class));
		verify(historyRepositoryMock).save(any(HistoryEntity.class));
		assertThat(e.getStatus()).isEqualTo(Status.I_AM_A_TEAPOT);
		assertThat(e.getMessage()).isEqualTo("I'm a teapot: Big and stout");
	}

	@Test
	void getHistory() {

		// Arrange
		final var response = new ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse();
		when(limeClientMock.getChatHistory(SESSION_ID)).thenReturn(response);

		// Act
		final var result = integration.getChatHistory(SESSION_ID);

		// Assert and verify
		verify(limeClientMock).getChatHistory(SESSION_ID);
		assertThat(result).isSameAs(response);
	}

	@Test
	void getHistoryThrowsException() {

		// Arrange
		final var exception = Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout");
		when(limeClientMock.getChatHistory(SESSION_ID)).thenThrow(exception);

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> integration.getChatHistory(SESSION_ID));

		// Assert and verify
		verify(limeClientMock).getChatHistory(SESSION_ID);
		assertThat(e).isSameAs(exception);
	}

}
