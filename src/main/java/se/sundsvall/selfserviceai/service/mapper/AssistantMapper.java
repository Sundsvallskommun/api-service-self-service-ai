package se.sundsvall.selfserviceai.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

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
import se.sundsvall.selfserviceai.integration.eneo.model.AskResponse;
import se.sundsvall.selfserviceai.integration.eneo.model.CompletionModel;
import se.sundsvall.selfserviceai.integration.eneo.model.FilePublic;

public class AssistantMapper {
	private AssistantMapper() {}

	public static SessionResponse toSessionResponse(final String assistantId, final AskResponse askResponse) {
		return ofNullable(askResponse)
			.map(askResponse1 -> SessionResponse.builder()
				.withAssistantId(assistantId)
				.withSessionId(ofNullable(askResponse1.sessionId()).map(UUID::toString).orElse(null))
				.build())
			.orElse(SessionResponse.builder()
				.withAssistantId(assistantId)
				.build());
	}

	public static QuestionResponse toQuestionResponse(final AskResponse askResponse) {
		return ofNullable(askResponse)
			.map(askResponse1 -> QuestionResponse.builder()
				.withAnswer(askResponse1.answer())
				.withFiles(toFiles(askResponse1.files()))
				.withModel(toModel(askResponse1.completionModel()))
				.withQuestion(askResponse1.question())
				.withReferences(toReferences(askResponse1.references()))
				.withSessionId(ofNullable(askResponse1.sessionId()).map(UUID::toString).orElse(null))
				.withTools(toTools(askResponse1.tools()))
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
				.withCreatedAt(filePublic.createdAt())
				.withId(ofNullable(filePublic.id()).map(UUID::toString).orElse(null))
				.withMimeType(filePublic.mimeType())
				.withName(filePublic.name())
				.withSize(ofNullable(filePublic.size()).orElse(0))
				.withTranscription(filePublic.transcription())
				.withUpdatedAt(filePublic.updatedAt())
				.build())
			.orElse(null);
	}

	static Model toModel(final CompletionModel eneoModel) {
		return ofNullable(eneoModel)
			.map(completionModel -> Model.builder()
				.withBaseUrl(completionModel.baseUrl())
				.withCreatedAt(completionModel.createdAt())
				.withDeploymentName(completionModel.deploymentName())
				.withDeprecated(completionModel.isDeprecated())
				.withDescription(completionModel.description())
				.withFamily(completionModel.family())
				.withHfLink(completionModel.hfLink())
				.withHosting(completionModel.hosting())
				.withId(ofNullable(completionModel.id()).map(UUID::toString).orElse(null))
				.withName(completionModel.name())
				.withNickname(completionModel.nickname())
				.withNrBillionParameters(ofNullable(completionModel.nrBillionParameters()).orElse(0))
				.withOpenSource(completionModel.openSource())
				.withOrg(completionModel.org())
				.withOrgDefault(completionModel.isOrgDefault())
				.withOrgEnabled(completionModel.isOrgEnabled())
				.withReasoning(completionModel.reasoning())
				.withStability(completionModel.stability())
				.withTokenLimit(completionModel.tokenLimit())
				.withUpdatedAt(completionModel.updatedAt())
				.withVision(completionModel.vision())
				.build())
			.orElse(null);
	}

	static List<Reference> toReferences(final List<se.sundsvall.selfserviceai.integration.eneo.model.Reference> eneoReferences) {
		return ofNullable(eneoReferences).orElse(emptyList()).stream()
			.map(AssistantMapper::toReference)
			.filter(Objects::nonNull)
			.toList();
	}

	static Reference toReference(final se.sundsvall.selfserviceai.integration.eneo.model.Reference eneoReference) {
		return ofNullable(eneoReference)
			.map(reference -> Reference.builder()
				.withCreatedAt(reference.createdAt())
				.withGroupId(ofNullable(reference.groupId()).map(UUID::toString).orElse(null))
				.withId(ofNullable(reference.id()).map(UUID::toString).orElse(null))
				.withMetadata(toMetadata(reference.metadata()))
				.withScore(reference.score())
				.withUpdatedAt(reference.updatedAt())
				.withWebsiteId(ofNullable(reference.websiteId()).map(UUID::toString).orElse(null))
				.build())
			.orElse(null);
	}

	static Metadata toMetadata(final se.sundsvall.selfserviceai.integration.eneo.model.Metadata eneoMetadata) {
		return ofNullable(eneoMetadata)
			.map(metadata -> Metadata.builder()
				.withEmbeddingModelId(ofNullable(metadata.embeddingModelId()).map(UUID::toString).orElse(null))
				.withSize(metadata.size())
				.withTitle(metadata.title())
				.withUrl(metadata.url())
				.build())
			.orElse(null);
	}

	static Tools toTools(final se.sundsvall.selfserviceai.integration.eneo.model.Tools eneoTools) {
		return ofNullable(eneoTools)
			.map(tools -> Tools.builder()
				.withAssistants(toAssistants(tools.assistants()))
				.build())
			.orElse(null);
	}

	static List<Assistant> toAssistants(final List<se.sundsvall.selfserviceai.integration.eneo.model.Assistant> eneoAssistants) {
		return ofNullable(eneoAssistants).orElse(emptyList()).stream()
			.map(AssistantMapper::toAssistant)
			.filter(Objects::nonNull)
			.toList();
	}

	static Assistant toAssistant(final se.sundsvall.selfserviceai.integration.eneo.model.Assistant eneoAssistant) {
		return ofNullable(eneoAssistant)
			.map(assistant -> Assistant.builder()
				.withHandle(assistant.handle())
				.withId(ofNullable(assistant.id()).map(UUID::toString).orElse(null))
				.build())
			.orElse(null);
	}
}
