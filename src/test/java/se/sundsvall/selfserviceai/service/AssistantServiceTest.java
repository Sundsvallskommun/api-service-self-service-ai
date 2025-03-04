package se.sundsvall.selfserviceai.service;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.selfserviceai.api.model.SessionRequest;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;
import se.sundsvall.selfserviceai.integration.intric.IntricIntegration;
import se.sundsvall.selfserviceai.integration.intric.configuration.IntricProperties;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ASSISTANT_ID = "assistantId";

	@Mock
	private IntricIntegration intricIntegrationMock;

	@Mock
	private SessionRepository sessionRepositoryMock;

	@Mock
	private FileRepository fileRepositoryMock;

	@Mock
	private IntricProperties intricPropertiesMock;

	@InjectMocks
	private AssistantService assistantService;

	@Captor
	private ArgumentCaptor<SessionEntity> sessionEntityCaptor;

	@Captor
	private ArgumentCaptor<FileEntity> fileEntityCaptor;

	@Captor
	private ArgumentCaptor<InstalledBase> installedBaseCaptor;

	@AfterEach
	void verifyNoMoreMockInterations() {
		verifyNoMoreInteractions(intricIntegrationMock, sessionRepositoryMock, fileRepositoryMock, intricPropertiesMock);
	}

	@Test
	void createSession() {
		// Arrange
		final var sessionId = UUID.randomUUID();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(intricIntegrationMock.askAssistant(eq(ASSISTANT_ID), anyString())).thenReturn(AskResponse.builder()
			.withSessionId(sessionId)
			.build());

		// Act
		final var response = assistantService.createSession(MUNICIPALITY_ID);

		// Assert and verify
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).askAssistant(ASSISTANT_ID, "Påbörjar session");
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(response).isEqualTo(sessionId);
		assertThat(sessionEntityCaptor.getValue().getCreated()).isNull();
		assertThat(sessionEntityCaptor.getValue().getFiles()).isEmpty();
		assertThat(sessionEntityCaptor.getValue().getInitialized()).isNull();
		assertThat(sessionEntityCaptor.getValue().getInitiationStatus()).isNull();
		assertThat(sessionEntityCaptor.getValue().getLastAccessed()).isNull();
		assertThat(sessionEntityCaptor.getValue().getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(sessionEntityCaptor.getValue().getSessionId()).isEqualTo(sessionId.toString());
	}

	@Test
	void createSessionThrowsException() {
		// Arrange
		final var exception = Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout");

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(intricIntegrationMock.askAssistant(eq(ASSISTANT_ID), anyString())).thenThrow(exception);

		// Act
		final var e = assertThrows(ThrowableProblem.class, () -> assistantService.createSession(MUNICIPALITY_ID));

		// Assert and verify
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).askAssistant(ASSISTANT_ID, "Påbörjar session");

		assertThat(e).isSameAs(exception);
	}

	@Test
	void populateWithInformation() throws Exception {
		// Arrange
		final var partyId = "partyId";
		final var customerEngagementOrgId = "ustomerEngagementOrgId";
		final var sessionId = UUID.randomUUID();
		final var sessionRequest = SessionRequest.builder()
			.withPartyId(partyId)
			.withCustomerEngagementOrgId(customerEngagementOrgId)
			.build();
		final var fileId = UUID.randomUUID();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.build();

		when(sessionRepositoryMock.findById(sessionId.toString())).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.uploadFile(any(InstalledBase.class))).thenReturn(fileId);
		when(fileRepositoryMock.save(any(FileEntity.class))).then(args -> args.getArgument(0));

		// Act
		assistantService.populateWithInformation(sessionId, sessionRequest);

		// Assert and verify
		verify(sessionRepositoryMock).findById(sessionId.toString());
		verify(intricIntegrationMock).uploadFile(installedBaseCaptor.capture());
		verify(fileRepositoryMock).save(fileEntityCaptor.capture());
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(installedBaseCaptor.getValue().getPartyId()).isEqualTo(partyId);
		assertThat(fileEntityCaptor.getValue().getFileId()).isEqualTo(fileId.toString());
		assertThat(sessionEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFiles()).hasSize(1).extracting(FileEntity::getFileId).containsExactly(fileId.toString());
			assertThat(entity.getInitialized()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
			assertThat(entity.getInitiationStatus()).isEqualTo("Successfully initialized");
		});
	}

	@Test
	void populateWithInformationWhenExceptionIsThrown() throws Exception {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var exception = Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout");
		final var sessionRequest = SessionRequest.builder().build();
		final var sessionEntity = SessionEntity.builder().build();

		when(sessionRepositoryMock.findById(anyString())).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.uploadFile(any(InstalledBase.class))).thenThrow(exception);

		// Act
		assistantService.populateWithInformation(sessionId, sessionRequest);

		// Assert and verify
		verify(sessionRepositoryMock).findById(sessionId.toString());
		verify(intricIntegrationMock).uploadFile(any(InstalledBase.class));
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(sessionEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFiles()).isEmpty();
			assertThat(entity.getInitialized()).isNull();
			assertThat(entity.getInitiationStatus()).isEqualTo("Initialization failed");
		});
	}

	@Test
	void populateWithInformationForNonExistingSession() throws Exception {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var sessionRequest = SessionRequest.builder().build();

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> assistantService.populateWithInformation(sessionId, sessionRequest));

		// Assert and verify
		verify(sessionRepositoryMock).findById(sessionId.toString());

		assertThat(exception.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: Session with id '%s' could not be found".formatted(sessionId));
	}

	@Test
	void isSessionReadyForReadySession() {
		// Arrange
		final var sessionId = UUID.randomUUID();

		when(sessionRepositoryMock.existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(sessionId.toString(), MUNICIPALITY_ID)).thenReturn(true);

		// Act
		final var result = assistantService.isSessionReady(MUNICIPALITY_ID, sessionId);

		// Assert and verify
		verify(sessionRepositoryMock).existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(sessionId.toString(), MUNICIPALITY_ID);

		assertThat(result).isTrue();
	}

	@Test
	void isSessionReadyForNonReadySession() {
		// Arrange
		final var sessionId = UUID.randomUUID();

		// Act
		final var result = assistantService.isSessionReady(MUNICIPALITY_ID, sessionId);

		// Assert and verify
		verify(sessionRepositoryMock).existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(sessionId.toString(), MUNICIPALITY_ID);

		assertThat(result).isFalse();
	}

	@Test
	void askQuestionToReadySession() {
		// Arrange
		final var question = "question";
		final var answer = "answer";
		final var sessionId = UUID.randomUUID();
		final var fileId = UUID.randomUUID();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.withInitialized(OffsetDateTime.now())
			.withFiles(List.of(
				FileEntity.builder()
					.withFileId(fileId.toString())
					.build()))
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.askFollowUp(ASSISTANT_ID, sessionId.toString(), question, List.of(fileId.toString()))).thenReturn(Optional.of(AskResponse.builder().withAnswer(answer).build()));

		// Act
		final var result = assistantService.askQuestion(MUNICIPALITY_ID, sessionId, question);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).askFollowUp(ASSISTANT_ID, sessionId.toString(), question, List.of(fileId.toString()));
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(result.getAnswer()).isEqualTo(answer);
		assertThat(sessionEntityCaptor.getValue()).isSameAs(sessionEntity);
		assertThat(sessionEntityCaptor.getValue().getLastAccessed()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
	}

	@Test
	void askQuestionToReadySessionWhenIntricDoesNotReturnResponse() {
		// Arrange
		final var question = "question";
		final var sessionId = UUID.randomUUID();
		final var fileId = UUID.randomUUID();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.withInitialized(OffsetDateTime.now())
			.withFiles(List.of(
				FileEntity.builder()
					.withFileId(fileId.toString())
					.build()))
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.askFollowUp(ASSISTANT_ID, sessionId.toString(), question, List.of(fileId.toString()))).thenReturn(Optional.empty());

		// Act
		final var result = assistantService.askQuestion(MUNICIPALITY_ID, sessionId, question);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).askFollowUp(ASSISTANT_ID, sessionId.toString(), question, List.of(fileId.toString()));
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(result).isNull();
		assertThat(sessionEntityCaptor.getValue()).isSameAs(sessionEntity);
		assertThat(sessionEntityCaptor.getValue().getLastAccessed()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
	}

	@Test
	void askQuestionToNonReadySession() {
		// Arrange
		final var question = "question";
		final var sessionId = UUID.randomUUID();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.build();

		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));

		// Act
		final var result = assistantService.askQuestion(MUNICIPALITY_ID, sessionId, question);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);

		assertThat(result).isNotNull();
		assertThat(result.getAnswer()).isEqualTo("Assistant is not ready yet");
	}

	@Test
	void askQuestionToNonExistingSession() {
		// Arrange
		final var question = "question";
		final var sessionId = UUID.randomUUID();

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> assistantService.askQuestion(MUNICIPALITY_ID, sessionId, question));

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);

		assertThat(exception.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: Session with id '%s' could not be found".formatted(sessionId));
	}

	@Test
	void deleteSessionWithFiles() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var fileId = UUID.randomUUID();
		final var fileEntity = FileEntity.builder()
			.withFileId(fileId.toString())
			.build();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.withInitialized(OffsetDateTime.now())
			.withFiles(new ArrayList<>(List.of(fileEntity)))
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.deleteFile(fileId.toString())).thenReturn(true);
		when(intricIntegrationMock.deleteSession(ASSISTANT_ID, sessionId.toString())).thenReturn(true);

		// Act
		assistantService.deleteSession(MUNICIPALITY_ID, sessionId);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).deleteFile(fileId.toString());
		verify(fileRepositoryMock).delete(fileEntity);
		verify(intricIntegrationMock).deleteSession(ASSISTANT_ID, sessionId.toString());
		verify(sessionRepositoryMock).delete(sessionEntity);
	}

	@Test
	void deleteSessionWithoutFiles() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.withInitialized(OffsetDateTime.now())
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.deleteSession(ASSISTANT_ID, sessionId.toString())).thenReturn(true);

		// Act
		assistantService.deleteSession(MUNICIPALITY_ID, sessionId);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).deleteSession(ASSISTANT_ID, sessionId.toString());
		verify(sessionRepositoryMock).delete(sessionEntity);
	}

	@Test
	void deleteSessionWhenFilesNotSuccessfullyDeleted() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var fileId = UUID.randomUUID();
		final var fileEntity = FileEntity.builder()
			.withFileId(fileId.toString())
			.build();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.withInitialized(OffsetDateTime.now())
			.withFiles(new ArrayList<>(List.of(fileEntity)))
			.build();

		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));

		// Act
		assistantService.deleteSession(MUNICIPALITY_ID, sessionId);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);
		verify(intricIntegrationMock).deleteFile(fileId.toString());
		verify(fileRepositoryMock, never()).delete(fileEntity);
		verify(sessionRepositoryMock, never()).delete(sessionEntity);
	}

	@Test
	void deleteSessionWhenSessionNotSuccessfullyDeleted() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.withInitialized(OffsetDateTime.now())
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));

		// Act
		assistantService.deleteSession(MUNICIPALITY_ID, sessionId);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).deleteSession(ASSISTANT_ID, sessionId.toString());
		verify(sessionRepositoryMock, never()).delete(sessionEntity);
	}

	@Test
	void deleteSessionForNonExistingSession() {
		// Arrange
		final var sessionId = UUID.randomUUID();
		SessionEntity.builder()
			.withSessionId(sessionId.toString())
			.withInitialized(OffsetDateTime.now())
			.build();

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> assistantService.deleteSession(MUNICIPALITY_ID, sessionId));

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(sessionId.toString(), MUNICIPALITY_ID);

		assertThat(exception.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: Session with id '%s' could not be found".formatted(sessionId));
	}
}
