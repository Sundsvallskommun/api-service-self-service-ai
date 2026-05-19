package se.sundsvall.selfserviceai.service.mapper;

import generated.se.sundsvall.eneo.AskResponse;
import generated.se.sundsvall.eneo.CompletionModelPublic;
import generated.se.sundsvall.eneo.FilePublic;
import generated.se.sundsvall.eneo.InfoBlobAskAssistantPublic;
import generated.se.sundsvall.eneo.InfoBlobMetadata;
import generated.se.sundsvall.eneo.ToolAssistant;
import generated.se.sundsvall.eneo.UseTools;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import se.sundsvall.selfserviceai.api.model.Assistant;
import se.sundsvall.selfserviceai.api.model.File;
import se.sundsvall.selfserviceai.api.model.Metadata;
import se.sundsvall.selfserviceai.api.model.Model;
import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.api.model.Reference;
import se.sundsvall.selfserviceai.api.model.SessionResponse;
import se.sundsvall.selfserviceai.api.model.Tools;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class AssistantMapper {
	private AssistantMapper() {}

	public static SessionResponse toSessionResponse(final String assistantId, final AskResponse askResponse) {
		return ofNullable(askResponse)
			.map(askResponse1 -> SessionResponse.builder()
				.withAssistantId(assistantId)
				.withSessionId(ofNullable(askResponse1.getSessionId()).map(UUID::toString).orElse(null))
				.build())
			.orElse(SessionResponse.builder()
				.withAssistantId(assistantId)
				.build());
	}

	public static QuestionResponse toQuestionResponse(final AskResponse askResponse) {
		return ofNullable(askResponse)
			.map(askResponse1 -> QuestionResponse.builder()
				.withAnswer(askResponse1.getAnswer())
				.withFiles(toFiles(askResponse1.getFiles()))
				.withModel(toModel(askResponse1.getModel()))
				.withQuestion(askResponse1.getQuestion())
				.withReferences(toReferences(askResponse1.getReferences()))
				.withSessionId(ofNullable(askResponse1.getSessionId()).map(UUID::toString).orElse(null))
				.withTools(toTools(askResponse1.getTools()))
				.build())
			.orElse(null);
	}

	public static QuestionResponse toQuestionResponse(final String answer) {
		return QuestionResponse.builder()
			.withAnswer(answer)
			.build();
	}

	static List<File> toFiles(final List<FilePublic> eneoFiles) {
		return ofNullable(eneoFiles).orElse(emptyList()).stream()
			.map(AssistantMapper::toFile)
			.filter(Objects::nonNull)
			.toList();
	}

	static File toFile(final FilePublic eneoFile) {
		return ofNullable(eneoFile)
			.map(filePublic -> File.builder()
				.withCreatedAt(filePublic.getCreatedAt())
				.withId(ofNullable(filePublic.getId()).map(UUID::toString).orElse(null))
				.withMimeType(filePublic.getMimetype())
				.withName(filePublic.getName())
				.withSize(ofNullable(filePublic.getSize()).orElse(0))
				.withTranscription(filePublic.getTranscription())
				.withUpdatedAt(filePublic.getUpdatedAt())
				.build())
			.orElse(null);
	}

	static Model toModel(final CompletionModelPublic eneoModel) {
		return ofNullable(eneoModel)
			.map(completionModel -> Model.builder()
				.withBaseUrl(completionModel.getBaseUrl())
				.withCreatedAt(completionModel.getCreatedAt())
				.withDeploymentName(completionModel.getDeploymentName())
				.withDeprecated(completionModel.getIsDeprecated())
				.withDescription(completionModel.getDescription())
				.withFamily(completionModel.getFamily())
				.withHfLink(completionModel.getHfLink())
				.withHosting(completionModel.getHosting())
				.withId(ofNullable(completionModel.getId()).map(UUID::toString).orElse(null))
				.withName(completionModel.getName())
				.withNickname(completionModel.getNickname())
				.withNrBillionParameters(ofNullable(completionModel.getNrBillionParameters()).orElse(0))
				.withOpenSource(completionModel.getOpenSource())
				.withOrg(completionModel.getOrg())
				.withOrgDefault(completionModel.getIsOrgDefault())
				.withOrgEnabled(completionModel.getIsOrgEnabled())
				.withReasoning(completionModel.getReasoning())
				.withStability(completionModel.getStability())
				.withTokenLimit(completionModel.getTokenLimit())
				.withUpdatedAt(completionModel.getUpdatedAt())
				.withVision(completionModel.getVision())
				.build())
			.orElse(null);
	}

	static List<Reference> toReferences(final List<InfoBlobAskAssistantPublic> eneoReferences) {
		return ofNullable(eneoReferences).orElse(emptyList()).stream()
			.map(AssistantMapper::toReference)
			.filter(Objects::nonNull)
			.toList();
	}

	static Reference toReference(final InfoBlobAskAssistantPublic eneoReference) {
		return ofNullable(eneoReference)
			.map(reference -> Reference.builder()
				.withCreatedAt(reference.getCreatedAt())
				.withGroupId(ofNullable(reference.getGroupId()).map(UUID::toString).orElse(null))
				.withId(ofNullable(reference.getId()).map(UUID::toString).orElse(null))
				.withMetadata(toMetadata(reference.getMetadata()))
				.withScore(ofNullable(reference.getScore()).map(BigDecimal::intValue).orElse(0))
				.withUpdatedAt(reference.getUpdatedAt())
				.withWebsiteId(ofNullable(reference.getWebsiteId()).map(UUID::toString).orElse(null))
				.build())
			.orElse(null);
	}

	static Metadata toMetadata(final InfoBlobMetadata eneoMetadata) {
		return ofNullable(eneoMetadata)
			.map(metadata -> Metadata.builder()
				.withEmbeddingModelId(ofNullable(metadata.getEmbeddingModelId()).map(UUID::toString).orElse(null))
				.withSize(metadata.getSize())
				.withTitle(metadata.getTitle())
				.withUrl(metadata.getUrl())
				.build())
			.orElse(null);
	}

	static Tools toTools(final UseTools eneoTools) {
		return ofNullable(eneoTools)
			.map(tools -> Tools.builder()
				.withAssistants(toAssistants(tools.getAssistants()))
				.build())
			.orElse(null);
	}

	static List<Assistant> toAssistants(final List<ToolAssistant> eneoAssistants) {
		return ofNullable(eneoAssistants).orElse(emptyList()).stream()
			.map(AssistantMapper::toAssistant)
			.filter(Objects::nonNull)
			.toList();
	}

	static Assistant toAssistant(final ToolAssistant eneoAssistant) {
		return ofNullable(eneoAssistant)
			.map(assistant -> Assistant.builder()
				.withHandle(assistant.getHandle())
				.withId(ofNullable(assistant.getId()).map(UUID::toString).orElse(null))
				.build())
			.orElse(null);
	}
}
