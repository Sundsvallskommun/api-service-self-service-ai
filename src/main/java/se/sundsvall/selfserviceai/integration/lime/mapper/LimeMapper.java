package se.sundsvall.selfserviceai.integration.lime.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import se.sundsvall.selfserviceai.integration.intric.model.Message;
import se.sundsvall.selfserviceai.integration.intric.model.SessionPublic;

public class LimeMapper {
	private LimeMapper() {}

	public static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest toChatHistoryRequest(String identity, boolean isOrganization, int customerNbr, SessionPublic session) {
		return ofNullable(session)
			.map(s -> new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest()
				.chatSession(toChatSessionDto(s))
				.identitetsnummer(identity)
				.isOrganisation(isOrganization)
				.kundnummer(String.valueOf(customerNbr)))
			.orElse(null);
	}

	private static ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto toChatSessionDto(SessionPublic session) {
		return new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto()
			.createdAt(session.createdAt())
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
			.map(x -> new ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto()
				.answer(message.answer())
				.createdAt(message.createdAt())
				.files(message.files())
				.id(ofNullable(message.id())
					.map(UUID::toString)
					.orElse(null))
				.question(message.question())
				.updatedAt(message.updatedAt()))
			.orElse(null);
	}
}
