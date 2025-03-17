package se.sundsvall.selfserviceai.integration.lime;

import static se.sundsvall.selfserviceai.integration.lime.mapper.LimeMapper.toChatHistoryRequest;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse;
import se.sundsvall.selfserviceai.integration.intric.model.SessionPublic;

public class LimeIntegration {

	private final LimeClient limeClient;

	LimeIntegration(LimeClient limeClient) {
		this.limeClient = limeClient;
	}

	public ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse getChatHistory(String sessionId) {
		return limeClient.getChatHistory(sessionId);
	}

	public void saveChatHistory(String partyId, boolean isOrganization, int customerNbr, SessionPublic session) {
		limeClient.saveChatHistory(toChatHistoryRequest(partyId, isOrganization, customerNbr, session));
	}

}
