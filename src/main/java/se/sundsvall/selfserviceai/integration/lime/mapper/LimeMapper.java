package se.sundsvall.selfserviceai.integration.lime.mapper;

import generated.se.sundsvall.eneo.CompletionModel;
import generated.se.sundsvall.eneo.Message;
import generated.se.sundsvall.eneo.SessionPublic;
import generated.se.sundsvall.eneo.UseTools;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikCompletionModelDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikToolsDto;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class LimeMapper {
	private LimeMapper() {}

	public static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest toChatHistoryRequest(String partyId, String customerNbr, SessionPublic session) {
		return ofNullable(session)
			.map(s -> new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest()
				.chatSession(toChatSessionDto(s))
				.partyId(partyId)
				.kundnummer(customerNbr))
			.orElse(null);
	}

	private static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto toChatSessionDto(SessionPublic session) {
		return new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto()
			.createdAt(session.getCreatedAt())
			.feedback(session.getFeedback())
			.id(ofNullable(session.getId())
				.map(UUID::toString)
				.orElse(null))
			.messages(toMessageDtos(session.getMessages()))
			.name(session.getName())
			.updatedAt(session.getUpdatedAt());
	}

	private static List<ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto> toMessageDtos(List<Message> messages) {
		return ofNullable(messages).orElse(emptyList()).stream()
			.map(LimeMapper::toMessageDto)
			.filter(Objects::nonNull)
			.toList();
	}

	private static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto toMessageDto(Message message) {
		return ofNullable(message)
			.map(m -> new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto()
				.answer(m.getAnswer())
				.completionModel(toCompletionModelDto(m.getCompletionModel()))
				.createdAt(m.getCreatedAt())
				.files(m.getFiles())
				.id(ofNullable(m.getId())
					.map(UUID::toString)
					.orElse(null))
				.references(m.getReferences())
				.tools(toToolsDto(m.getTools()))
				.question(m.getQuestion())
				.updatedAt(m.getUpdatedAt()))
			.orElse(null);
	}

	private static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikCompletionModelDto toCompletionModelDto(CompletionModel completionModel) {
		return ofNullable(completionModel)
			.map(c -> new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikCompletionModelDto()
				.createdAt(c.getCreatedAt())
				.deploymentName(c.getDeploymentName())
				.description(c.getDescription())
				.family(c.getFamily())
				.hfLink(c.getHfLink())
				.hosting(c.getHosting())
				.id(ofNullable(c.getId())
					.map(UUID::toString)
					.orElse(null))
				.isDeprecated(c.getIsDeprecated())
				.isOrgEnabled(c.getIsOrgEnabled())
				.isOrgDefault(c.getIsOrgDefault())
				.name(c.getName())
				.nickname(c.getNickname())
				.nrBillionParameters(c.getNrBillionParameters())
				.stability(c.getStability())
				.openSource(c.getOpenSource())
				.org(c.getOrg())
				.tokenLimit(c.getTokenLimit())
				.vision(c.getVision())
				.updatedAt(c.getUpdatedAt()))
			.orElse(null);
	}

	private static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikToolsDto toToolsDto(UseTools tools) {
		return ofNullable(tools)
			.map(t -> new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikToolsDto()
				.assistants(t.getAssistants()))
			.orElse(null);
	}

}
