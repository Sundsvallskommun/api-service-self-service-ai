package se.sundsvall.selfserviceai.integration.lime;

import static se.sundsvall.selfserviceai.integration.lime.mapper.LimeMapper.toChatHistoryRequest;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse;
import org.springframework.stereotype.Component;
import se.sundsvall.selfserviceai.integration.intric.model.SessionPublic;

@Component
public class LimeIntegration {

	private final LimeClient limeClient;

	LimeIntegration(LimeClient limeClient) {
		this.limeClient = limeClient;
	}

	public ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse getChatHistory(String sessionId) {
		return limeClient.getChatHistory(sessionId);
	}

	public void saveChatHistory(String partyId, String customerNbr, SessionPublic session) {
		limeClient.saveChatHistory(toChatHistoryRequest(partyId, customerNbr, session));
	}

}
