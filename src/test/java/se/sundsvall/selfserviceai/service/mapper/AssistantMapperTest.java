package se.sundsvall.selfserviceai.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.selfserviceai.integration.eneo.model.AskResponse;
import se.sundsvall.selfserviceai.integration.eneo.model.Assistant;
import se.sundsvall.selfserviceai.integration.eneo.model.CompletionModel;
import se.sundsvall.selfserviceai.integration.eneo.model.FilePublic;
import se.sundsvall.selfserviceai.integration.eneo.model.Metadata;
import se.sundsvall.selfserviceai.integration.eneo.model.Reference;
import se.sundsvall.selfserviceai.integration.eneo.model.Tools;

@ExtendWith(MockitoExtension.class)
class AssistantMapperTest {

	@Mock
	private AskResponse askResponseMock;

	@Mock
	private Assistant assistantMock;

	@Mock
	private CompletionModel completionModelMock;

	@Mock
	private FilePublic fileMock;

	@Mock
	private Metadata metadataMock;

	@Mock
	private Reference referenceMock;

	@Mock
	private Tools toolsMock;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(
			askResponseMock,
			assistantMock,
			completionModelMock,
			fileMock,
			metadataMock,
			referenceMock,
			toolsMock);
	}

	@Test
	void toSessionResponse() {
		// Arrange
		final var assistantId = "assistantId";
		final var sessionId = UUID.randomUUID();

		when(askResponseMock.sessionId()).thenReturn(sessionId);

		// Act
		final var result = AssistantMapper.toSessionResponse(assistantId, askResponseMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getAssistantId()).isEqualTo(assistantId);
		assertThat(result.getSessionId()).isEqualTo(sessionId.toString());

		verify(askResponseMock).sessionId();
	}

	@Test
	void toSessionResponseFromNull() {
		// Act
		final var result = AssistantMapper.toSessionResponse(null, null);

		// Assert
		assertThat(result).isNotNull().hasAllNullFieldsOrProperties();
	}

	@Test
	void toQuestionResponseFromAskResponse() {
		// Arrange
		final var answer = "yes if this is an answer.";
		final var sessionId = UUID.randomUUID();
		final var question = "is this a question?";

		when(askResponseMock.answer()).thenReturn(answer);
		when(askResponseMock.completionModel()).thenReturn(completionModelMock);
		when(askResponseMock.files()).thenReturn(List.of(fileMock));
		when(askResponseMock.question()).thenReturn(question);
		when(askResponseMock.references()).thenReturn(List.of(referenceMock));
		when(referenceMock.metadata()).thenReturn(metadataMock);
		when(askResponseMock.sessionId()).thenReturn(sessionId);
		when(askResponseMock.tools()).thenReturn(toolsMock);
		when(toolsMock.assistants()).thenReturn(List.of(assistantMock));

		// Act
		final var result = AssistantMapper.toQuestionResponse(askResponseMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getAnswer()).isEqualTo(answer);
		assertThat(result.getSessionId()).isEqualTo(sessionId.toString());
		assertThat(result.getQuestion()).isEqualTo(question);

		verifyAskResponseMockInteractions();
		verifyCompletionModelMockInteractions();
		verifyFileMockInteractions();
		verifyMetadataMockInteractions();
		verifyReferenceMockInteractions();
		verifyToolsMockInteractions();
		verifyAssistantInteractions();
	}

	@Test
	void toQuestionResponseFromNull() {
		// Act and assert
		assertThat(AssistantMapper.toQuestionResponse((AskResponse) null)).isNull();
	}

	@Test
	void toQuestionResponseFromString() {
		// Arrange
		final var answer = "answer";

		// Act
		final var result = AssistantMapper.toQuestionResponse(answer);

		// Assert
		assertThat(result)
			.hasAllNullFieldsOrPropertiesExcept("answer")
			.hasFieldOrPropertyWithValue("answer", answer);
	}

	@Test
	void toFiles() {
		// Arrange
		final var createdAt = OffsetDateTime.now().minusDays(7);
		final var id = UUID.randomUUID();
		final var mimeType = "mimeType";
		final var name = "name";
		final var size = 666;
		final var transcription = "transcription";
		final var updatedAt = OffsetDateTime.now();
		final var input = new ArrayList<>(List.of(fileMock));

		input.add(null); // To verify filtering of null values

		when(fileMock.createdAt()).thenReturn(createdAt);
		when(fileMock.id()).thenReturn(id);
		when(fileMock.mimeType()).thenReturn(mimeType);
		when(fileMock.name()).thenReturn(name);
		when(fileMock.size()).thenReturn(size);
		when(fileMock.transcription()).thenReturn(transcription);
		when(fileMock.updatedAt()).thenReturn(updatedAt);

		// Act
		final var result = AssistantMapper.toFiles(input);

		// Assert and verify
		assertThat(result).hasSize(1).satisfiesExactly(f -> {
			assertThat(f).hasNoNullFieldsOrProperties();
			assertThat(f.getCreatedAt()).isEqualTo(createdAt);
			assertThat(f.getId()).isEqualTo(id.toString());
			assertThat(f.getMimeType()).isEqualTo(mimeType);
			assertThat(f.getName()).isEqualTo(name);
			assertThat(f.getSize()).isEqualTo(size);
			assertThat(f.getTranscription()).isEqualTo(transcription);
			assertThat(f.getUpdatedAt()).isEqualTo(updatedAt);
		});

		verifyFileMockInteractions();
	}

	@Test
	void toFilesFromNull() {
		// Act and assert
		assertThat(AssistantMapper.toFiles(null)).isEmpty();
	}

	@Test
	void toFile() {
		// Arrange
		final var createdAt = OffsetDateTime.now().minusDays(7);
		final var id = UUID.randomUUID();
		final var mimeType = "mimeType";
		final var name = "name";
		final var size = 666;
		final var transcription = "transcription";
		final var updatedAt = OffsetDateTime.now();

		when(fileMock.createdAt()).thenReturn(createdAt);
		when(fileMock.id()).thenReturn(id);
		when(fileMock.mimeType()).thenReturn(mimeType);
		when(fileMock.name()).thenReturn(name);
		when(fileMock.size()).thenReturn(size);
		when(fileMock.transcription()).thenReturn(transcription);
		when(fileMock.updatedAt()).thenReturn(updatedAt);

		// Act
		final var result = AssistantMapper.toFile(fileMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCreatedAt()).isEqualTo(createdAt);
		assertThat(result.getId()).isEqualTo(id.toString());
		assertThat(result.getMimeType()).isEqualTo(mimeType);
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getSize()).isEqualTo(size);
		assertThat(result.getTranscription()).isEqualTo(transcription);
		assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);

		verifyFileMockInteractions();
	}

	@Test
	void toFileFromNull() {
		// Act and assert
		assertThat(AssistantMapper.toFile(null)).isNull();
	}

	@Test
	void toModel() {
		// Arrange
		final var baseUrl = "baseUrl";
		final var createdAt = OffsetDateTime.now().minusDays(14);
		final var deploymentName = "deploymentName";
		final var description = "description";
		final var family = "family";
		final var hfLink = "hfLink";
		final var hosting = "hosting";
		final var id = UUID.randomUUID();
		final var deprecated = true;
		final var orgDefault = true;
		final var orgEnabled = true;
		final var name = "name";
		final var nickname = "nickname";
		final var nrBillionParameters = 123;
		final var openSource = true;
		final var org = "org";
		final var reasoning = true;
		final var stability = "stability";
		final var tokenLimit = 1024;
		final var updatedAt = OffsetDateTime.now().minusDays(7);
		final var vision = true;

		when(completionModelMock.baseUrl()).thenReturn(baseUrl);
		when(completionModelMock.createdAt()).thenReturn(createdAt);
		when(completionModelMock.deploymentName()).thenReturn(deploymentName);
		when(completionModelMock.description()).thenReturn(description);
		when(completionModelMock.family()).thenReturn(family);
		when(completionModelMock.hfLink()).thenReturn(hfLink);
		when(completionModelMock.hosting()).thenReturn(hosting);
		when(completionModelMock.id()).thenReturn(id);
		when(completionModelMock.isDeprecated()).thenReturn(deprecated);
		when(completionModelMock.isOrgDefault()).thenReturn(orgDefault);
		when(completionModelMock.isOrgEnabled()).thenReturn(orgEnabled);
		when(completionModelMock.name()).thenReturn(name);
		when(completionModelMock.nickname()).thenReturn(nickname);
		when(completionModelMock.nrBillionParameters()).thenReturn(nrBillionParameters);
		when(completionModelMock.openSource()).thenReturn(openSource);
		when(completionModelMock.org()).thenReturn(org);
		when(completionModelMock.reasoning()).thenReturn(reasoning);
		when(completionModelMock.stability()).thenReturn(stability);
		when(completionModelMock.tokenLimit()).thenReturn(tokenLimit);
		when(completionModelMock.updatedAt()).thenReturn(updatedAt);
		when(completionModelMock.vision()).thenReturn(vision);

		// Act
		final var result = AssistantMapper.toModel(completionModelMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getBaseUrl()).isEqualTo(baseUrl);
		assertThat(result.getCreatedAt()).isEqualTo(createdAt);
		assertThat(result.getDeploymentName()).isEqualTo(deploymentName);
		assertThat(result.getDeprecated()).isEqualTo(deprecated);
		assertThat(result.getDescription()).isEqualTo(description);
		assertThat(result.getFamily()).isEqualTo(family);
		assertThat(result.getHfLink()).isEqualTo(hfLink);
		assertThat(result.getHosting()).isEqualTo(hosting);
		assertThat(result.getId()).isEqualTo(id.toString());
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getNickname()).isEqualTo(nickname);
		assertThat(result.getNrBillionParameters()).isEqualTo(nrBillionParameters);
		assertThat(result.getOpenSource()).isEqualTo(openSource);
		assertThat(result.getOrg()).isEqualTo(org);
		assertThat(result.getStability()).isEqualTo(stability);
		assertThat(result.getTokenLimit()).isEqualTo(tokenLimit);
		assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);

		verifyCompletionModelMockInteractions();
	}

	@Test
	void toModelFromNull() {
		// Act and assert
		assertThat(AssistantMapper.toModel(null)).isNull();
	}

	@Test
	void toReferences() {
		// Arrange
		final var createdAt = OffsetDateTime.now().minusDays(7);
		final var groupId = UUID.randomUUID();
		final var id = UUID.randomUUID();
		final var score = 789;
		final var updatedAt = OffsetDateTime.now();
		final var websiteId = UUID.randomUUID();
		final var input = new ArrayList<>(List.of(referenceMock));

		input.add(null); // To verify filtering of null values

		when(referenceMock.createdAt()).thenReturn(createdAt);
		when(referenceMock.groupId()).thenReturn(groupId);
		when(referenceMock.id()).thenReturn(id);
		when(referenceMock.metadata()).thenReturn(metadataMock);
		when(referenceMock.score()).thenReturn(score);
		when(referenceMock.updatedAt()).thenReturn(updatedAt);
		when(referenceMock.websiteId()).thenReturn(websiteId);

		// Act
		final var result = AssistantMapper.toReferences(input);

		// Assert and verify
		assertThat(result).hasSize(1).satisfiesExactly(r -> {
			assertThat(r).hasNoNullFieldsOrProperties();
			assertThat(r.getCreatedAt()).isEqualTo(createdAt);
			assertThat(r.getGroupId()).isEqualTo(groupId.toString());
			assertThat(r.getId()).isEqualTo(id.toString());
			assertThat(r.getMetadata()).isNotNull().hasAllNullFieldsOrPropertiesExcept("size").hasFieldOrPropertyWithValue("size", 0);
			assertThat(r.getScore()).isEqualTo(score);
			assertThat(r.getUpdatedAt()).isEqualTo(updatedAt);
			assertThat(r.getWebsiteId()).isEqualTo(websiteId.toString());
		});

		verifyReferenceMockInteractions();
		verifyMetadataMockInteractions();
	}

	@Test
	void toReference() {
		// Arrange
		final var createdAt = OffsetDateTime.now().minusDays(7);
		final var groupId = UUID.randomUUID();
		final var id = UUID.randomUUID();
		final var score = 789;
		final var updatedAt = OffsetDateTime.now();
		final var websiteId = UUID.randomUUID();

		when(referenceMock.createdAt()).thenReturn(createdAt);
		when(referenceMock.groupId()).thenReturn(groupId);
		when(referenceMock.id()).thenReturn(id);
		when(referenceMock.metadata()).thenReturn(metadataMock);
		when(referenceMock.score()).thenReturn(score);
		when(referenceMock.updatedAt()).thenReturn(updatedAt);
		when(referenceMock.websiteId()).thenReturn(websiteId);

		// Act
		final var result = AssistantMapper.toReference(referenceMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCreatedAt()).isEqualTo(createdAt);
		assertThat(result.getGroupId()).isEqualTo(groupId.toString());
		assertThat(result.getId()).isEqualTo(id.toString());
		assertThat(result.getMetadata()).isNotNull().hasAllNullFieldsOrPropertiesExcept("size").hasFieldOrPropertyWithValue("size", 0);
		assertThat(result.getScore()).isEqualTo(score);
		assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
		assertThat(result.getWebsiteId()).isEqualTo(websiteId.toString());

		verifyReferenceMockInteractions();
		verifyMetadataMockInteractions();
	}

	@Test
	void toReferenceWhenMetadataNull() {
		// Arrange
		final var createdAt = OffsetDateTime.now().minusDays(7);
		final var groupId = UUID.randomUUID();
		final var id = UUID.randomUUID();
		final var score = 789;
		final var updatedAt = OffsetDateTime.now();
		final var websiteId = UUID.randomUUID();

		when(referenceMock.createdAt()).thenReturn(createdAt);
		when(referenceMock.groupId()).thenReturn(groupId);
		when(referenceMock.id()).thenReturn(id);
		when(referenceMock.score()).thenReturn(score);
		when(referenceMock.updatedAt()).thenReturn(updatedAt);
		when(referenceMock.websiteId()).thenReturn(websiteId);

		// Act
		final var result = AssistantMapper.toReference(referenceMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrPropertiesExcept("metadata");
		assertThat(result.getCreatedAt()).isEqualTo(createdAt);
		assertThat(result.getGroupId()).isEqualTo(groupId.toString());
		assertThat(result.getId()).isEqualTo(id.toString());
		assertThat(result.getScore()).isEqualTo(score);
		assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
		assertThat(result.getWebsiteId()).isEqualTo(websiteId.toString());

		verifyReferenceMockInteractions();
	}

	@Test
	void toReferenceFromNull() {
		// Act and assert
		assertThat(AssistantMapper.toReference(null)).isNull();
	}

	@Test
	void toMetadata() {
		// Arrange
		final var embeddingModelId = UUID.randomUUID();
		final var size = 258;
		final var title = "title";
		final var url = "url";

		when(metadataMock.embeddingModelId()).thenReturn(embeddingModelId);
		when(metadataMock.size()).thenReturn(size);
		when(metadataMock.title()).thenReturn(title);
		when(metadataMock.url()).thenReturn(url);

		// Act
		final var result = AssistantMapper.toMetadata(metadataMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getEmbeddingModelId()).isEqualTo(embeddingModelId.toString());
		assertThat(result.getSize()).isEqualTo(size);
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getUrl()).isEqualTo(url);

		verifyMetadataMockInteractions();
	}

	@Test
	void toMetadataFromNull() {
		assertThat(AssistantMapper.toMetadata(null)).isNull();
	}

	@Test
	void toTools() {
		final var handle = "handle";
		final var id = UUID.randomUUID();

		when(toolsMock.assistants()).thenReturn(List.of(assistantMock));
		when(assistantMock.handle()).thenReturn(handle);
		when(assistantMock.id()).thenReturn(id);

		// Act
		final var result = AssistantMapper.toTools(toolsMock);

		// Assert and verify
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getAssistants()).hasSize(1).satisfiesExactly(a -> {
			assertThat(a.getHandle()).isEqualTo(handle);
			assertThat(a.getId()).isEqualTo(id.toString());
		});

		verifyToolsMockInteractions();
		verifyAssistantInteractions();
	}

	@Test
	void toToolsFromNull() {
		// Act and assert
		assertThat(AssistantMapper.toTools(null)).isNull();
	}

	@Test
	void toAssistants() {
		// Arrange
		final var handle = "handle";
		final var id = UUID.randomUUID();
		final var input = new ArrayList<>(List.of(assistantMock));

		input.add(null); // To verify filtering of null values

		when(assistantMock.handle()).thenReturn(handle);
		when(assistantMock.id()).thenReturn(id);

		// Act
		final var result = AssistantMapper.toAssistants(input);

		// Assert and verify
		assertThat(result).hasSize(1).satisfiesExactly(entry -> {
			assertThat(entry.getHandle()).isEqualTo(handle);
			assertThat(entry.getId()).isEqualTo(id.toString());
		});

		verifyAssistantInteractions();
	}

	@Test
	void toAssistantsFromNull() {
		// Act and assert
		assertThat(AssistantMapper.toAssistants(null)).isEmpty();
	}

	@Test
	void toAssistant() {
		// Arrange
		final var handle = "handle";
		final var id = UUID.randomUUID();

		when(assistantMock.handle()).thenReturn(handle);
		when(assistantMock.id()).thenReturn(id);

		// Act
		final var result = AssistantMapper.toAssistant(assistantMock);

		// Assert and verify
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getHandle()).isEqualTo(handle);
		assertThat(result.getId()).isEqualTo(id.toString());

		verifyAssistantInteractions();
	}

	@Test
	void toAssistantFromNull() {
		// Act and assert
		assertThat(AssistantMapper.toAssistant(null)).isNull();
	}

	@Test
	void toAssistantFromEmptyInput() {
		// Act, assert and verify
		assertThat(AssistantMapper.toAssistant(assistantMock)).isNotNull().hasAllNullFieldsOrProperties();

		verifyAssistantInteractions();
	}

	private void verifyCompletionModelMockInteractions() {
		verify(completionModelMock).baseUrl();
		verify(completionModelMock).createdAt();
		verify(completionModelMock).deploymentName();
		verify(completionModelMock).description();
		verify(completionModelMock).family();
		verify(completionModelMock).hfLink();
		verify(completionModelMock).hosting();
		verify(completionModelMock).id();
		verify(completionModelMock).isDeprecated();
		verify(completionModelMock).isOrgDefault();
		verify(completionModelMock).isOrgEnabled();
		verify(completionModelMock).name();
		verify(completionModelMock).nickname();
		verify(completionModelMock).nrBillionParameters();
		verify(completionModelMock).openSource();
		verify(completionModelMock).org();
		verify(completionModelMock).reasoning();
		verify(completionModelMock).stability();
		verify(completionModelMock).tokenLimit();
		verify(completionModelMock).updatedAt();
		verify(completionModelMock).vision();
	}

	private void verifyAskResponseMockInteractions() {
		verify(askResponseMock).answer();
		verify(askResponseMock).completionModel();
		verify(askResponseMock).files();
		verify(askResponseMock).question();
		verify(askResponseMock).references();
		verify(askResponseMock).sessionId();
		verify(askResponseMock).tools();
	}

	private void verifyFileMockInteractions() {
		verify(fileMock).createdAt();
		verify(fileMock).id();
		verify(fileMock).mimeType();
		verify(fileMock).name();
		verify(fileMock).size();
		verify(fileMock).transcription();
		verify(fileMock).updatedAt();
	}

	private void verifyMetadataMockInteractions() {
		verify(metadataMock).embeddingModelId();
		verify(metadataMock).size();
		verify(metadataMock).title();
		verify(metadataMock).url();
	}

	private void verifyReferenceMockInteractions() {
		verify(referenceMock).createdAt();
		verify(referenceMock).groupId();
		verify(referenceMock).id();
		verify(referenceMock).metadata();
		verify(referenceMock).score();
		verify(referenceMock).updatedAt();
		verify(referenceMock).websiteId();
	}

	private void verifyToolsMockInteractions() {
		verify(toolsMock).assistants();
	}

	private void verifyAssistantInteractions() {
		verify(assistantMock).handle();
		verify(assistantMock).id();
	}
}
