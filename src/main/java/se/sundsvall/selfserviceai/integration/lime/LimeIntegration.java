package se.sundsvall.selfserviceai.integration.lime;

import static se.sundsvall.selfserviceai.integration.db.mapper.DatabaseMapper.toHistoryEntity;
import static se.sundsvall.selfserviceai.integration.lime.mapper.LimeMapper.toChatHistoryRequest;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse;
import org.springframework.stereotype.Component;
import se.sundsvall.selfserviceai.integration.db.HistoryRepository;
import se.sundsvall.selfserviceai.integration.eneo.model.SessionPublic;
import se.sundsvall.selfserviceai.service.util.JsonBuilder;

@Component
public class LimeIntegration {

	private final HistoryRepository historyRepository;
	private final JsonBuilder jsonBuilder;
	private final LimeClient limeClient;

	LimeIntegration(final HistoryRepository historyRepository, final JsonBuilder jsonBuilder, final LimeClient limeClient) {
		this.historyRepository = historyRepository;
		this.jsonBuilder = jsonBuilder;
		this.limeClient = limeClient;
	}

	public ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse getChatHistory(final String sessionId) {
		return limeClient.getChatHistory(sessionId);
	}

	public void saveChatHistory(final String partyId, final String customerNbr, final SessionPublic session) {
		final var chatHistory = toChatHistoryRequest(partyId, customerNbr, session);
		try {
			limeClient.saveChatHistory(chatHistory);
		} catch (final Exception e) {
			final var limeHistory = jsonBuilder.toJsonString(chatHistory);
			final var historyEntity = toHistoryEntity(customerNbr, limeHistory, partyId, session.id());

			historyRepository.save(historyEntity);
		}
	}
}
