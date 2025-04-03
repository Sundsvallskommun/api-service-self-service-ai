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
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;
import se.sundsvall.selfserviceai.integration.intric.model.CompletionModel;
import se.sundsvall.selfserviceai.integration.intric.model.FilePublic;

public class AssistantMapper {
	private AssistantMapper() {}

	public static SessionResponse toSessionResponse(final String assistantId, final AskResponse askResponse) {
		return ofNullable(askResponse)
			.map(r -> SessionResponse.builder()
				.withAssistantId(assistantId)
				.withSessionId(ofNullable(r.sessionId()).map(UUID::toString).orElse(null))
				.build())
			.orElse(SessionResponse.builder()
				.withAssistantId(assistantId)
				.build());
	}

	public static QuestionResponse toQuestionResponse(final AskResponse askResponse) {
		return ofNullable(askResponse)
			.map(r -> QuestionResponse.builder()
				.withAnswer(r.answer())
				.withFiles(toFiles(r.files()))
				.withModel(toModel(r.completionModel()))
				.withQuestion(r.question())
				.withReferences(toReferences(r.references()))
				.withSessionId(ofNullable(r.sessionId()).map(UUID::toString).orElse(null))
				.withTools(toTools(r.tools()))
				.build())
			.orElse(null);
	}

	public static QuestionResponse toQuestionResponse(final String answer) {
		return QuestionResponse.builder()
			.withAnswer(answer)
			.build();
	}

	static List<File> toFiles(List<FilePublic> intricFiles) {
		return ofNullable(intricFiles).orElse(emptyList()).stream()
			.map(AssistantMapper::toFile)
			.filter(Objects::nonNull)
			.toList();
	}

	static File toFile(FilePublic intricFile) {
		return ofNullable(intricFile)
			.map(f -> File.builder()
				.withCreatedAt(f.createdAt())
				.withId(ofNullable(f.id()).map(UUID::toString).orElse(null))
				.withMimeType(f.mimeType())
				.withName(f.name())
				.withSize(ofNullable(f.size()).orElse(0))
				.withTranscription(f.transcription())
				.withUpdatedAt(f.updatedAt())
				.build())
			.orElse(null);
	}

	static Model toModel(CompletionModel intricModel) {
		return ofNullable(intricModel)
			.map(m -> Model.builder()
				.withBaseUrl(m.baseUrl())
				.withCreatedAt(m.createdAt())
				.withDeploymentName(m.deploymentName())
				.withDeprecated(m.isDeprecated())
				.withDescription(m.description())
				.withFamily(m.family())
				.withHfLink(m.hfLink())
				.withHosting(m.hosting())
				.withId(ofNullable(m.id()).map(UUID::toString).orElse(null))
				.withName(m.name())
				.withNickname(m.nickname())
				.withNrBillionParameters(ofNullable(m.nrBillionParameters()).orElse(0))
				.withOpenSource(m.openSource())
				.withOrg(m.org())
				.withOrgDefault(m.isOrgDefault())
				.withOrgEnabled(m.isOrgEnabled())
				.withReasoning(m.reasoning())
				.withStability(m.stability())
				.withTokenLimit(m.tokenLimit())
				.withUpdatedAt(m.updatedAt())
				.withVision(m.vision())
				.build())
			.orElse(null);
	}

	static List<Reference> toReferences(List<se.sundsvall.selfserviceai.integration.intric.model.Reference> intricReferences) {
		return ofNullable(intricReferences).orElse(emptyList()).stream()
			.map(AssistantMapper::toReference)
			.filter(Objects::nonNull)
			.toList();
	}

	static Reference toReference(se.sundsvall.selfserviceai.integration.intric.model.Reference intricReference) {
		return ofNullable(intricReference)
			.map(r -> Reference.builder()
				.withCreatedAt(r.createdAt())
				.withGroupId(ofNullable(r.groupId()).map(UUID::toString).orElse(null))
				.withId(ofNullable(r.id()).map(UUID::toString).orElse(null))
				.withMetadata(toMetadata(r.metadata()))
				.withScore(r.score())
				.withUpdatedAt(r.updatedAt())
				.withWebsiteId(ofNullable(r.websiteId()).map(UUID::toString).orElse(null))
				.build())
			.orElse(null);
	}

	static Metadata toMetadata(se.sundsvall.selfserviceai.integration.intric.model.Metadata intricMetadata) {
		return ofNullable(intricMetadata)
			.map(m -> Metadata.builder()
				.withEmbeddingModelId(ofNullable(m.embeddingModelId()).map(UUID::toString).orElse(null))
				.withSize(m.size())
				.withTitle(m.title())
				.withUrl(m.url())
				.build())
			.orElse(null);
	}

	static Tools toTools(se.sundsvall.selfserviceai.integration.intric.model.Tools intricTools) {
		return ofNullable(intricTools)
			.map(t -> Tools.builder()
				.withAssistants(toAssistants(t.assistants()))
				.build())
			.orElse(null);
	}

	static List<Assistant> toAssistants(List<se.sundsvall.selfserviceai.integration.intric.model.Assistant> intricAssistants) {
		return ofNullable(intricAssistants).orElse(emptyList()).stream()
			.map(AssistantMapper::toAssistant)
			.filter(Objects::nonNull)
			.toList();
	}

	static Assistant toAssistant(se.sundsvall.selfserviceai.integration.intric.model.Assistant intricAssistant) {
		return ofNullable(intricAssistant)
			.map(a -> Assistant.builder()
				.withHandle(a.handle())
				.withId(ofNullable(a.id()).map(UUID::toString).orElse(null))
				.build())
			.orElse(null);
	}
}
