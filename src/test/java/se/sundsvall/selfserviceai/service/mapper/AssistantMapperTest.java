package se.sundsvall.selfserviceai.service.mapper;

import generated.se.sundsvall.eneo.AskResponse;
import generated.se.sundsvall.eneo.CompletionModelPublic;
import generated.se.sundsvall.eneo.FilePublic;
import generated.se.sundsvall.eneo.InfoBlobAskAssistantPublic;
import generated.se.sundsvall.eneo.InfoBlobMetadata;
import generated.se.sundsvall.eneo.ToolAssistant;
import generated.se.sundsvall.eneo.UseTools;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssistantMapperTest {

	@Mock
	private AskResponse askResponseMock;

	@Mock
	private ToolAssistant assistantMock;

	@Mock
	private CompletionModelPublic completionModelMock;

	@Mock
	private FilePublic fileMock;

	@Mock
	private InfoBlobMetadata metadataMock;

	@Mock
	private InfoBlobAskAssistantPublic referenceMock;

	@Mock
	private UseTools toolsMock;

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

		when(askResponseMock.getSessionId()).thenReturn(sessionId);

		// Act
		final var result = AssistantMapper.toSessionResponse(assistantId, askResponseMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getAssistantId()).isEqualTo(assistantId);
		assertThat(result.getSessionId()).isEqualTo(sessionId.toString());

		verify(askResponseMock).getSessionId();
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

		when(askResponseMock.getAnswer()).thenReturn(answer);
		when(askResponseMock.getModel()).thenReturn(completionModelMock);
		when(askResponseMock.getFiles()).thenReturn(List.of(fileMock));
		when(askResponseMock.getQuestion()).thenReturn(question);
		when(askResponseMock.getReferences()).thenReturn(List.of(referenceMock));
		when(referenceMock.getMetadata()).thenReturn(metadataMock);
		when(askResponseMock.getSessionId()).thenReturn(sessionId);
		when(askResponseMock.getTools()).thenReturn(toolsMock);
		when(toolsMock.getAssistants()).thenReturn(List.of(assistantMock));

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

		when(fileMock.getCreatedAt()).thenReturn(createdAt);
		when(fileMock.getId()).thenReturn(id);
		when(fileMock.getMimetype()).thenReturn(mimeType);
		when(fileMock.getName()).thenReturn(name);
		when(fileMock.getSize()).thenReturn(size);
		when(fileMock.getTranscription()).thenReturn(transcription);
		when(fileMock.getUpdatedAt()).thenReturn(updatedAt);

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

		when(fileMock.getCreatedAt()).thenReturn(createdAt);
		when(fileMock.getId()).thenReturn(id);
		when(fileMock.getMimetype()).thenReturn(mimeType);
		when(fileMock.getName()).thenReturn(name);
		when(fileMock.getSize()).thenReturn(size);
		when(fileMock.getTranscription()).thenReturn(transcription);
		when(fileMock.getUpdatedAt()).thenReturn(updatedAt);

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

		when(completionModelMock.getBaseUrl()).thenReturn(baseUrl);
		when(completionModelMock.getCreatedAt()).thenReturn(createdAt);
		when(completionModelMock.getDeploymentName()).thenReturn(deploymentName);
		when(completionModelMock.getDescription()).thenReturn(description);
		when(completionModelMock.getFamily()).thenReturn(family);
		when(completionModelMock.getHfLink()).thenReturn(hfLink);
		when(completionModelMock.getHosting()).thenReturn(hosting);
		when(completionModelMock.getId()).thenReturn(id);
		when(completionModelMock.getIsDeprecated()).thenReturn(deprecated);
		when(completionModelMock.getIsOrgDefault()).thenReturn(orgDefault);
		when(completionModelMock.getIsOrgEnabled()).thenReturn(orgEnabled);
		when(completionModelMock.getName()).thenReturn(name);
		when(completionModelMock.getNickname()).thenReturn(nickname);
		when(completionModelMock.getNrBillionParameters()).thenReturn(nrBillionParameters);
		when(completionModelMock.getOpenSource()).thenReturn(openSource);
		when(completionModelMock.getOrg()).thenReturn(org);
		when(completionModelMock.getReasoning()).thenReturn(reasoning);
		when(completionModelMock.getStability()).thenReturn(stability);
		when(completionModelMock.getTokenLimit()).thenReturn(tokenLimit);
		when(completionModelMock.getUpdatedAt()).thenReturn(updatedAt);
		when(completionModelMock.getVision()).thenReturn(vision);

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
		final var score = new BigDecimal("789");
		final var updatedAt = OffsetDateTime.now();
		final var websiteId = UUID.randomUUID();
		final var input = new ArrayList<>(List.of(referenceMock));

		input.add(null); // To verify filtering of null values

		when(referenceMock.getCreatedAt()).thenReturn(createdAt);
		when(referenceMock.getGroupId()).thenReturn(groupId);
		when(referenceMock.getId()).thenReturn(id);
		when(referenceMock.getMetadata()).thenReturn(metadataMock);
		when(referenceMock.getScore()).thenReturn(score);
		when(referenceMock.getUpdatedAt()).thenReturn(updatedAt);
		when(referenceMock.getWebsiteId()).thenReturn(websiteId);

		// Act
		final var result = AssistantMapper.toReferences(input);

		// Assert and verify
		assertThat(result).hasSize(1).satisfiesExactly(r -> {
			assertThat(r).hasNoNullFieldsOrProperties();
			assertThat(r.getCreatedAt()).isEqualTo(createdAt);
			assertThat(r.getGroupId()).isEqualTo(groupId.toString());
			assertThat(r.getId()).isEqualTo(id.toString());
			assertThat(r.getMetadata()).isNotNull().hasAllNullFieldsOrPropertiesExcept("size").hasFieldOrPropertyWithValue("size", 0);
			assertThat(r.getScore()).isEqualTo(score.intValue());
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
		final var score = new BigDecimal("789");
		final var updatedAt = OffsetDateTime.now();
		final var websiteId = UUID.randomUUID();

		when(referenceMock.getCreatedAt()).thenReturn(createdAt);
		when(referenceMock.getGroupId()).thenReturn(groupId);
		when(referenceMock.getId()).thenReturn(id);
		when(referenceMock.getMetadata()).thenReturn(metadataMock);
		when(referenceMock.getScore()).thenReturn(score);
		when(referenceMock.getUpdatedAt()).thenReturn(updatedAt);
		when(referenceMock.getWebsiteId()).thenReturn(websiteId);

		// Act
		final var result = AssistantMapper.toReference(referenceMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getCreatedAt()).isEqualTo(createdAt);
		assertThat(result.getGroupId()).isEqualTo(groupId.toString());
		assertThat(result.getId()).isEqualTo(id.toString());
		assertThat(result.getMetadata()).isNotNull().hasAllNullFieldsOrPropertiesExcept("size").hasFieldOrPropertyWithValue("size", 0);
		assertThat(result.getScore()).isEqualTo(score.intValue());
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
		final var score = new BigDecimal("789");
		final var updatedAt = OffsetDateTime.now();
		final var websiteId = UUID.randomUUID();

		when(referenceMock.getCreatedAt()).thenReturn(createdAt);
		when(referenceMock.getGroupId()).thenReturn(groupId);
		when(referenceMock.getId()).thenReturn(id);
		when(referenceMock.getScore()).thenReturn(score);
		when(referenceMock.getUpdatedAt()).thenReturn(updatedAt);
		when(referenceMock.getWebsiteId()).thenReturn(websiteId);

		// Act
		final var result = AssistantMapper.toReference(referenceMock);

		// Assert and verify
		assertThat(result).hasNoNullFieldsOrPropertiesExcept("metadata");
		assertThat(result.getCreatedAt()).isEqualTo(createdAt);
		assertThat(result.getGroupId()).isEqualTo(groupId.toString());
		assertThat(result.getId()).isEqualTo(id.toString());
		assertThat(result.getScore()).isEqualTo(score.intValue());
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

		when(metadataMock.getEmbeddingModelId()).thenReturn(embeddingModelId);
		when(metadataMock.getSize()).thenReturn(size);
		when(metadataMock.getTitle()).thenReturn(title);
		when(metadataMock.getUrl()).thenReturn(url);

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

		when(toolsMock.getAssistants()).thenReturn(List.of(assistantMock));
		when(assistantMock.getHandle()).thenReturn(handle);
		when(assistantMock.getId()).thenReturn(id);

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

		when(assistantMock.getHandle()).thenReturn(handle);
		when(assistantMock.getId()).thenReturn(id);

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

		when(assistantMock.getHandle()).thenReturn(handle);
		when(assistantMock.getId()).thenReturn(id);

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
		verify(completionModelMock).getBaseUrl();
		verify(completionModelMock).getCreatedAt();
		verify(completionModelMock).getDeploymentName();
		verify(completionModelMock).getDescription();
		verify(completionModelMock).getFamily();
		verify(completionModelMock).getHfLink();
		verify(completionModelMock).getHosting();
		verify(completionModelMock).getId();
		verify(completionModelMock).getIsDeprecated();
		verify(completionModelMock).getIsOrgDefault();
		verify(completionModelMock).getIsOrgEnabled();
		verify(completionModelMock).getName();
		verify(completionModelMock).getNickname();
		verify(completionModelMock).getNrBillionParameters();
		verify(completionModelMock).getOpenSource();
		verify(completionModelMock).getOrg();
		verify(completionModelMock).getReasoning();
		verify(completionModelMock).getStability();
		verify(completionModelMock).getTokenLimit();
		verify(completionModelMock).getUpdatedAt();
		verify(completionModelMock).getVision();
	}

	private void verifyAskResponseMockInteractions() {
		verify(askResponseMock).getAnswer();
		verify(askResponseMock).getModel();
		verify(askResponseMock).getFiles();
		verify(askResponseMock).getQuestion();
		verify(askResponseMock).getReferences();
		verify(askResponseMock).getSessionId();
		verify(askResponseMock).getTools();
	}

	private void verifyFileMockInteractions() {
		verify(fileMock).getCreatedAt();
		verify(fileMock).getId();
		verify(fileMock).getMimetype();
		verify(fileMock).getName();
		verify(fileMock).getSize();
		verify(fileMock).getTranscription();
		verify(fileMock).getUpdatedAt();
	}

	private void verifyMetadataMockInteractions() {
		verify(metadataMock).getEmbeddingModelId();
		verify(metadataMock).getSize();
		verify(metadataMock).getTitle();
		verify(metadataMock).getUrl();
	}

	private void verifyReferenceMockInteractions() {
		verify(referenceMock).getCreatedAt();
		verify(referenceMock).getGroupId();
		verify(referenceMock).getId();
		verify(referenceMock).getMetadata();
		verify(referenceMock).getScore();
		verify(referenceMock).getUpdatedAt();
		verify(referenceMock).getWebsiteId();
	}

	private void verifyToolsMockInteractions() {
		verify(toolsMock).getAssistants();
	}

	private void verifyAssistantInteractions() {
		verify(assistantMock).getHandle();
		verify(assistantMock).getId();
	}
}
