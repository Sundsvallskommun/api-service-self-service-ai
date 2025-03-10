package se.sundsvall.selfserviceai.service;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
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

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.invoices.Invoice;
import generated.se.sundsvall.invoices.InvoiceStatus;
import generated.se.sundsvall.invoices.InvoiceType;
import generated.se.sundsvall.measurementdata.Category;
import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementPoints;
import generated.se.sundsvall.measurementdata.MeasurementSerie;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.selfserviceai.api.model.SessionRequest;
import se.sundsvall.selfserviceai.integration.agreement.AgreementIntegration;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;
import se.sundsvall.selfserviceai.integration.installedbase.InstalledbaseIntegration;
import se.sundsvall.selfserviceai.integration.intric.IntricIntegration;
import se.sundsvall.selfserviceai.integration.intric.configuration.IntricProperties;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;
import se.sundsvall.selfserviceai.integration.invoices.InvoicesIntegration;
import se.sundsvall.selfserviceai.integration.measurementdata.MeasurementDataIntegration;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String ASSISTANT_ID = "assistantId";
	private static final UUID SESSION_ID = UUID.randomUUID();
	private static final String PARTY_ID = UUID.randomUUID().toString();
	private static final String FACILITY_ID = UUID.randomUUID().toString();
	private static final String CUSTOMER_ENGAGEMENT_ORG_ID = "customerEngagementOrgId";

	@Mock
	private IntricProperties intricPropertiesMock;

	@Mock
	private IntricIntegration intricIntegrationMock;

	@Mock
	private AgreementIntegration agreementIntegrationMock;

	@Mock
	private InstalledbaseIntegration installedbaseIntegrationMock;

	@Mock
	private InvoicesIntegration invoicesIntegrationMock;

	@Mock
	private MeasurementDataIntegration measurementDataIntegrationMock;

	@Mock
	private SessionRepository sessionRepositoryMock;

	@Mock
	private FileRepository fileRepositoryMock;

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
		verifyNoMoreInteractions(
			intricPropertiesMock,
			intricIntegrationMock,
			agreementIntegrationMock,
			installedbaseIntegrationMock,
			invoicesIntegrationMock,
			measurementDataIntegrationMock,
			sessionRepositoryMock,
			fileRepositoryMock);
	}

	@Test
	void createSession() {
		// Arrange
		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(intricIntegrationMock.askAssistant(eq(ASSISTANT_ID), anyString())).thenReturn(AskResponse.builder()
			.withSessionId(SESSION_ID)
			.build());

		// Act
		final var response = assistantService.createSession(MUNICIPALITY_ID);

		// Assert and verify
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).askAssistant(ASSISTANT_ID, "Påbörjar session");
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(response).isEqualTo(SESSION_ID);
		assertThat(sessionEntityCaptor.getValue().getCreated()).isNull();
		assertThat(sessionEntityCaptor.getValue().getFiles()).isEmpty();
		assertThat(sessionEntityCaptor.getValue().getInitialized()).isNull();
		assertThat(sessionEntityCaptor.getValue().getInitiationStatus()).isNull();
		assertThat(sessionEntityCaptor.getValue().getLastAccessed()).isNull();
		assertThat(sessionEntityCaptor.getValue().getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(sessionEntityCaptor.getValue().getSessionId()).isEqualTo(SESSION_ID.toString());
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

	private static final Stream<Arguments> informationArgumentProvider() {
		return Stream.of(
			Arguments.of(
				null,
				emptyList(),
				emptyList()),
			Arguments.of(
				List.of(new Agreement()
					.agreementId(String.valueOf(RandomUtils.secure().randomInt()))
					.category(generated.se.sundsvall.agreement.Category.ELECTRICITY)
					.facilityId(FACILITY_ID)),
				List.of(new Invoice()
					.invoiceNumber(String.valueOf(RandomUtils.secure().randomInt()))
					.invoiceType(InvoiceType.INVOICE)
					.invoiceStatus(InvoiceStatus.SENT)
					.facilityId(FACILITY_ID)),
				List.of(new Data()
					.facilityId(FACILITY_ID)
					.addMeasurementSeriesItem(new MeasurementSerie()
						.addMeasurementPointsItem(new MeasurementPoints()))
					.category(Category.ELECTRICITY)))

		);
	}

	@ParameterizedTest
	@MethodSource("informationArgumentProvider")
	void populateWithInformation(List<Agreement> agreements, List<Invoice> invoices, List<Data> measurementDatas) {
		// Arrange
		final var fileId = UUID.randomUUID();
		final var installedBaseCustomer = new InstalledBaseCustomer()
			.items(List.of(new InstalledBaseItem()
				.facilityId(FACILITY_ID)))
			.partyId(PARTY_ID);
		final var sessionRequest = SessionRequest.builder()
			.withPartyId(PARTY_ID)
			.withCustomerEngagementOrgId(CUSTOMER_ENGAGEMENT_ORG_ID)
			.build();
		final var sessionEntity = SessionEntity.builder()
			.withMunicipalityId(MUNICIPALITY_ID)
			.withSessionId(SESSION_ID.toString())
			.build();

		when(sessionRepositoryMock.findById(SESSION_ID.toString())).thenReturn(Optional.of(sessionEntity));
		when(agreementIntegrationMock.getAgreements(MUNICIPALITY_ID, PARTY_ID)).thenReturn(agreements);
		when(installedbaseIntegrationMock.getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID)).thenReturn(installedBaseCustomer);
		when(invoicesIntegrationMock.getInvoices(MUNICIPALITY_ID, PARTY_ID)).thenReturn(invoices);
		when(measurementDataIntegrationMock.getMeasurementData(MUNICIPALITY_ID, PARTY_ID, agreements)).thenReturn(measurementDatas);
		when(intricIntegrationMock.uploadFile(any(InstalledBase.class))).thenReturn(fileId);
		when(fileRepositoryMock.save(any(FileEntity.class))).then(args -> args.getArgument(0));

		// Act
		assistantService.populateWithInformation(SESSION_ID, sessionRequest);

		// Assert and verify
		verify(sessionRepositoryMock).findById(SESSION_ID.toString());
		verify(agreementIntegrationMock).getAgreements(MUNICIPALITY_ID, PARTY_ID);
		verify(installedbaseIntegrationMock).getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID);
		verify(invoicesIntegrationMock).getInvoices(MUNICIPALITY_ID, PARTY_ID);
		verify(measurementDataIntegrationMock).getMeasurementData(MUNICIPALITY_ID, PARTY_ID, agreements);
		verify(intricIntegrationMock).uploadFile(installedBaseCaptor.capture());
		verify(fileRepositoryMock).save(fileEntityCaptor.capture());
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(installedBaseCaptor.getValue().getPartyId()).isEqualTo(PARTY_ID);
		assertThat(fileEntityCaptor.getValue().getFileId()).isEqualTo(fileId.toString());
		assertThat(sessionEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFiles()).hasSize(1).extracting(FileEntity::getFileId).containsExactly(fileId.toString());
			assertThat(entity.getInitialized()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
			assertThat(entity.getInitiationStatus()).isEqualTo("Successfully initialized");

			assertListcontent(agreements, invoices, measurementDatas);
		});
	}

	private void assertListcontent(List<Agreement> agreements, List<Invoice> invoices, List<Data> measurementDatas) {
		assertThat(Optional.ofNullable(agreements).orElse(emptyList()).stream()
			.map(Agreement::getAgreementId)
			.toList())
			.isEqualTo(installedBaseCaptor.getValue().getFacilities().stream()
				.map(Facility::getAgreements).flatMap(List::stream)
				.map(se.sundsvall.selfserviceai.integration.intric.model.filecontent.Agreement::getAgreementId)
				.toList());

		assertThat(invoices.stream()
			.map(Invoice::getInvoiceNumber)
			.toList())
			.isEqualTo(installedBaseCaptor.getValue().getFacilities().stream()
				.map(Facility::getInvoices).flatMap(List::stream)
				.map(se.sundsvall.selfserviceai.integration.intric.model.filecontent.Invoice::getInvoiceNumber)
				.toList());

		assertThat(measurementDatas.stream()
			.map(Data::getCategory)
			.map(Category::toString)
			.toList())
			.isEqualTo(installedBaseCaptor.getValue().getFacilities().stream()
				.map(Facility::getMeasurements).flatMap(List::stream)
				.map(se.sundsvall.selfserviceai.integration.intric.model.filecontent.MeasurementData::getCategory)
				.toList());
	}

	@Test
	void populateWithInformationWhenNoInstalledbaseResponse() {
		// Arrange
		Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout");
		final var sessionEntity = SessionEntity.builder()
			.withMunicipalityId(MUNICIPALITY_ID)
			.build();
		final var sessionRequest = SessionRequest.builder()
			.withCustomerEngagementOrgId(CUSTOMER_ENGAGEMENT_ORG_ID)
			.withPartyId(PARTY_ID)
			.build();

		when(sessionRepositoryMock.findById(anyString())).thenReturn(Optional.of(sessionEntity));

		// Act
		assistantService.populateWithInformation(SESSION_ID, sessionRequest);

		// Assert and verify
		verify(sessionRepositoryMock).findById(SESSION_ID.toString());
		verify(agreementIntegrationMock).getAgreements(MUNICIPALITY_ID, PARTY_ID);
		verify(installedbaseIntegrationMock).getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID);
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(sessionEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFiles()).isEmpty();
			assertThat(entity.getInitialized()).isNull();
			assertThat(entity.getInitiationStatus()).isEqualTo("Initialization failed");
		});
	}

	@Test
	void populateWithInformationWhenIntricThrowsException() {
		// Arrange
		final var exception = Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout");
		final var sessionEntity = SessionEntity.builder()
			.withMunicipalityId(MUNICIPALITY_ID)
			.build();
		final var sessionRequest = SessionRequest.builder()
			.withCustomerEngagementOrgId(CUSTOMER_ENGAGEMENT_ORG_ID)
			.withPartyId(PARTY_ID)
			.build();

		when(sessionRepositoryMock.findById(anyString())).thenReturn(Optional.of(sessionEntity));
		when(installedbaseIntegrationMock.getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID)).thenReturn(new InstalledBaseCustomer());
		when(intricIntegrationMock.uploadFile(any(InstalledBase.class))).thenThrow(exception);

		// Act
		assistantService.populateWithInformation(SESSION_ID, sessionRequest);

		// Assert and verify
		verify(sessionRepositoryMock).findById(SESSION_ID.toString());
		verify(agreementIntegrationMock).getAgreements(MUNICIPALITY_ID, PARTY_ID);
		verify(installedbaseIntegrationMock).getInstalledbase(MUNICIPALITY_ID, PARTY_ID, CUSTOMER_ENGAGEMENT_ORG_ID);
		verify(invoicesIntegrationMock).getInvoices(MUNICIPALITY_ID, PARTY_ID);
		verify(measurementDataIntegrationMock).getMeasurementData(MUNICIPALITY_ID, PARTY_ID, emptyList());
		verify(intricIntegrationMock).uploadFile(any(InstalledBase.class));
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(sessionEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFiles()).isEmpty();
			assertThat(entity.getInitialized()).isNull();
			assertThat(entity.getInitiationStatus()).isEqualTo("Initialization failed");
		});
	}

	@Test
	void populateWithInformationForNonExistingSession() {
		// Arrange
		final var sessionRequest = SessionRequest.builder().build();

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> assistantService.populateWithInformation(SESSION_ID, sessionRequest));

		// Assert and verify
		verify(sessionRepositoryMock).findById(SESSION_ID.toString());

		assertThat(exception.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: Session with id '%s' could not be found".formatted(SESSION_ID));
	}

	@Test
	void isSessionReadyForReadySession() {
		// Arrange
		when(sessionRepositoryMock.existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(SESSION_ID.toString(), MUNICIPALITY_ID)).thenReturn(true);

		// Act
		final var result = assistantService.isSessionReady(MUNICIPALITY_ID, SESSION_ID);

		// Assert and verify
		verify(sessionRepositoryMock).existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(SESSION_ID.toString(), MUNICIPALITY_ID);

		assertThat(result).isTrue();
	}

	@Test
	void isSessionReadyForNonReadySession() {
		// Act
		final var result = assistantService.isSessionReady(MUNICIPALITY_ID, SESSION_ID);

		// Assert and verify
		verify(sessionRepositoryMock).existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(SESSION_ID.toString(), MUNICIPALITY_ID);

		assertThat(result).isFalse();
	}

	@Test
	void askQuestionToReadySession() {
		// Arrange
		final var question = "question";
		final var answer = "answer";
		final var fileId = UUID.randomUUID();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(SESSION_ID.toString())
			.withInitialized(OffsetDateTime.now())
			.withFiles(List.of(
				FileEntity.builder()
					.withFileId(fileId.toString())
					.build()))
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.askFollowUp(ASSISTANT_ID, SESSION_ID.toString(), question, List.of(fileId.toString()))).thenReturn(Optional.of(AskResponse.builder().withAnswer(answer).build()));

		// Act
		final var result = assistantService.askQuestion(MUNICIPALITY_ID, SESSION_ID, question);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).askFollowUp(ASSISTANT_ID, SESSION_ID.toString(), question, List.of(fileId.toString()));
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(result.getAnswer()).isEqualTo(answer);
		assertThat(sessionEntityCaptor.getValue()).isSameAs(sessionEntity);
		assertThat(sessionEntityCaptor.getValue().getLastAccessed()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
	}

	@Test
	void askQuestionToReadySessionWhenIntricDoesNotReturnResponse() {
		// Arrange
		final var question = "question";
		final var fileId = UUID.randomUUID();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(SESSION_ID.toString())
			.withInitialized(OffsetDateTime.now())
			.withFiles(List.of(
				FileEntity.builder()
					.withFileId(fileId.toString())
					.build()))
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.askFollowUp(ASSISTANT_ID, SESSION_ID.toString(), question, List.of(fileId.toString()))).thenReturn(Optional.empty());

		// Act
		final var result = assistantService.askQuestion(MUNICIPALITY_ID, SESSION_ID, question);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).askFollowUp(ASSISTANT_ID, SESSION_ID.toString(), question, List.of(fileId.toString()));
		verify(sessionRepositoryMock).save(sessionEntityCaptor.capture());

		assertThat(result).isNull();
		assertThat(sessionEntityCaptor.getValue()).isSameAs(sessionEntity);
		assertThat(sessionEntityCaptor.getValue().getLastAccessed()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
	}

	@Test
	void askQuestionToNonReadySession() {
		// Arrange
		final var question = "question";
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(SESSION_ID.toString())
			.build();

		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));

		// Act
		final var result = assistantService.askQuestion(MUNICIPALITY_ID, SESSION_ID, question);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);

		assertThat(result).isNotNull();
		assertThat(result.getAnswer()).isEqualTo("Assistant is not ready yet");
	}

	@Test
	void askQuestionToNonExistingSession() {
		// Arrange
		final var question = "question";

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> assistantService.askQuestion(MUNICIPALITY_ID, SESSION_ID, question));

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);

		assertThat(exception.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: Session with id '%s' could not be found".formatted(SESSION_ID));
	}

	@Test
	void deleteSessionWithFiles() {
		// Arrange
		final var fileId = UUID.randomUUID();
		final var fileEntity = FileEntity.builder()
			.withFileId(fileId.toString())
			.build();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(SESSION_ID.toString())
			.withInitialized(OffsetDateTime.now())
			.withFiles(new ArrayList<>(List.of(fileEntity)))
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.deleteFile(fileId.toString())).thenReturn(true);
		when(intricIntegrationMock.deleteSession(ASSISTANT_ID, SESSION_ID.toString())).thenReturn(true);

		// Act
		assistantService.deleteSessionById(MUNICIPALITY_ID, SESSION_ID);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).deleteFile(fileId.toString());
		verify(fileRepositoryMock).delete(fileEntity);
		verify(intricIntegrationMock).deleteSession(ASSISTANT_ID, SESSION_ID.toString());
		verify(sessionRepositoryMock).delete(sessionEntity);
	}

	@Test
	void deleteSessionWithoutFiles() {
		// Arrange
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(SESSION_ID.toString())
			.withInitialized(OffsetDateTime.now())
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));
		when(intricIntegrationMock.deleteSession(ASSISTANT_ID, SESSION_ID.toString())).thenReturn(true);

		// Act
		assistantService.deleteSessionById(MUNICIPALITY_ID, SESSION_ID);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).deleteSession(ASSISTANT_ID, SESSION_ID.toString());
		verify(sessionRepositoryMock).delete(sessionEntity);
	}

	@Test
	void deleteSessionWhenFilesNotSuccessfullyDeleted() {
		// Arrange
		final var fileId = UUID.randomUUID();
		final var fileEntity = FileEntity.builder()
			.withFileId(fileId.toString())
			.build();
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(SESSION_ID.toString())
			.withInitialized(OffsetDateTime.now())
			.withFiles(new ArrayList<>(List.of(fileEntity)))
			.build();

		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));

		// Act
		assistantService.deleteSessionById(MUNICIPALITY_ID, SESSION_ID);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);
		verify(intricIntegrationMock).deleteFile(fileId.toString());
		verify(fileRepositoryMock, never()).delete(fileEntity);
		verify(sessionRepositoryMock, never()).delete(sessionEntity);
	}

	@Test
	void deleteSessionWhenSessionNotSuccessfullyDeleted() {
		// Arrange
		final var sessionEntity = SessionEntity.builder()
			.withSessionId(SESSION_ID.toString())
			.withInitialized(OffsetDateTime.now())
			.build();

		when(intricPropertiesMock.assistantId()).thenReturn(ASSISTANT_ID);
		when(sessionRepositoryMock.findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID)).thenReturn(Optional.of(sessionEntity));

		// Act
		assistantService.deleteSessionById(MUNICIPALITY_ID, SESSION_ID);

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);
		verify(intricPropertiesMock).assistantId();
		verify(intricIntegrationMock).deleteSession(ASSISTANT_ID, SESSION_ID.toString());
		verify(sessionRepositoryMock, never()).delete(sessionEntity);
	}

	@Test
	void deleteSessionForNonExistingSession() {
		// Arrange
		SessionEntity.builder()
			.withSessionId(SESSION_ID.toString())
			.withInitialized(OffsetDateTime.now())
			.build();

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> assistantService.deleteSessionById(MUNICIPALITY_ID, SESSION_ID));

		// Assert and verify
		verify(sessionRepositoryMock).findBySessionIdAndMunicipalityId(SESSION_ID.toString(), MUNICIPALITY_ID);

		assertThat(exception.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: Session with id '%s' could not be found".formatted(SESSION_ID));
	}
}
