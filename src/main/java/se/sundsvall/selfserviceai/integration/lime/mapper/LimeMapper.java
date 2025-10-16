package se.sundsvall.selfserviceai.integration.lime.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikCompletionModelDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikToolsDto;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import se.sundsvall.selfserviceai.integration.eneo.model.CompletionModel;
import se.sundsvall.selfserviceai.integration.eneo.model.Message;
import se.sundsvall.selfserviceai.integration.eneo.model.SessionPublic;
import se.sundsvall.selfserviceai.integration.eneo.model.Tools;

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
			.createdAt(session.createdAt())
			.feedback(session.feedback())
			.id(ofNullable(session.id())
				.map(UUID::toString)
				.orElse(null))
			.messages(toMessageDtos(session.messages()))
			.name(session.name())
			.updatedAt(session.updatedAt());
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
				.answer(m.answer())
				.completionModel(toCompletionModelDto(m.completionModel()))
				.createdAt(m.createdAt())
				.files(m.files())
				.id(ofNullable(m.id())
					.map(UUID::toString)
					.orElse(null))
				.references(m.references())
				.tools(toToolsDto(m.tools()))
				.question(m.question())
				.updatedAt(m.updatedAt()))
			.orElse(null);
	}

	private static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikCompletionModelDto toCompletionModelDto(CompletionModel completionModel) {
		return ofNullable(completionModel)
			.map(c -> new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikCompletionModelDto()
				.createdAt(c.createdAt())
				.deploymentName(c.deploymentName())
				.description(c.description())
				.family(c.family())
				.hfLink(c.hfLink())
				.hosting(c.hosting())
				.id(ofNullable(c.id())
					.map(UUID::toString)
					.orElse(null))
				.isDeprecated(c.isDeprecated())
				.isOrgEnabled(c.isOrgEnabled())
				.isOrgDefault(c.isOrgDefault())
				.name(c.name())
				.nickname(c.nickname())
				.nrBillionParameters(c.nrBillionParameters())
				.stability(c.stability())
				.openSource(c.openSource())
				.org(c.org())
				.tokenLimit(c.tokenLimit())
				.vision(c.vision())
				.updatedAt(c.updatedAt()))
			.orElse(null);
	}

	private static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikToolsDto toToolsDto(Tools tools) {
		return ofNullable(tools)
			.map(t -> new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikToolsDto()
				.assistants(t.assistants()))
			.orElse(null);
	}

}
