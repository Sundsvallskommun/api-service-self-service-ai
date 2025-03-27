package se.sundsvall.selfserviceai.service;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.selfserviceai.integration.db.DatabaseMapper.toFileEntity;
import static se.sundsvall.selfserviceai.integration.db.DatabaseMapper.toSessionEntity;
import static se.sundsvall.selfserviceai.integration.intric.mapper.IntricMapper.toIntricModel;
import static se.sundsvall.selfserviceai.service.mapper.AssistantMapper.toQuestionResponse;
import static se.sundsvall.selfserviceai.service.mapper.AssistantMapper.toSessionResponse;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import se.sundsvall.dept44.requestid.RequestId;
import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.api.model.SessionRequest;
import se.sundsvall.selfserviceai.api.model.SessionResponse;
import se.sundsvall.selfserviceai.integration.agreement.AgreementIntegration;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;
import se.sundsvall.selfserviceai.integration.installedbase.InstalledbaseIntegration;
import se.sundsvall.selfserviceai.integration.intric.IntricIntegration;
import se.sundsvall.selfserviceai.integration.intric.configuration.IntricProperties;
import se.sundsvall.selfserviceai.integration.intric.mapper.AgreementDecorator;
import se.sundsvall.selfserviceai.integration.intric.mapper.InvoiceDecorator;
import se.sundsvall.selfserviceai.integration.intric.mapper.MeasurementDecorator;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.IntricModel;
import se.sundsvall.selfserviceai.integration.invoices.InvoicesIntegration;
import se.sundsvall.selfserviceai.integration.lime.LimeIntegration;
import se.sundsvall.selfserviceai.integration.measurementdata.MeasurementDataIntegration;
import se.sundsvall.selfserviceai.service.mapper.AssistantMapper;

@Service
public class AssistantService {

	private static final Logger LOG = LoggerFactory.getLogger(AssistantService.class);
	private static final String ERROR_SESSION_NOT_FOUND = "Session with id '%s' could not be found";

	private final IntricProperties intricProperties;
	private final AgreementIntegration agreementIntegration;
	private final InstalledbaseIntegration installedbaseIntegration;
	private final IntricIntegration intricIntegration;
	private final InvoicesIntegration invoicesIntegration;
	private final LimeIntegration limeIntegration;
	private final MeasurementDataIntegration measurementDataIntegration;
	private final SessionRepository sessionRepository;
	private final FileRepository fileRepository;

	public AssistantService(
		final IntricProperties intricProperties,
		final AgreementIntegration agreementIntegration,
		final InstalledbaseIntegration installedbaseIntegration,
		final IntricIntegration intricIntegration,
		final InvoicesIntegration invoicesIntegration,
		final LimeIntegration limeIntegration,
		final MeasurementDataIntegration measurementDataIntegration,
		final SessionRepository sessionRepository,
		final FileRepository fileRepository) {

		this.intricProperties = intricProperties;
		this.agreementIntegration = agreementIntegration;
		this.installedbaseIntegration = installedbaseIntegration;
		this.intricIntegration = intricIntegration;
		this.invoicesIntegration = invoicesIntegration;
		this.limeIntegration = limeIntegration;
		this.measurementDataIntegration = measurementDataIntegration;
		this.sessionRepository = sessionRepository;
		this.fileRepository = fileRepository;
	}

	public SessionResponse createSession(String municipalityId, String partyId) {
		final var session = intricIntegration.askAssistant(intricProperties.assistantId(), "Påbörjar session för party id '%s'".formatted(partyId));
		sessionRepository.save(toSessionEntity(municipalityId, session.sessionId(), partyId));

		return toSessionResponse(intricProperties.assistantId(), session);
	}

	@Async
	@Transactional
	public void populateWithInformation(UUID sessionId, SessionRequest sessionRequest) {
		final var sessionEntity = sessionRepository.findById(sessionId.toString())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId)));

		try {
			final var municipalityId = sessionEntity.getMunicipalityId();
			final var partyId = sessionRequest.getPartyId();

			final var installedBases = installedbaseIntegration.getInstalledbases(municipalityId, partyId, sessionRequest.getCustomerEngagementOrgIds());

			if (isNotEmpty(installedBases)) {
				// Build file content
				final var intricModel = buildIntricModel(municipalityId, partyId, installedBases);

				// Save information in intric and update database with id of stored file
				final var fileId = intricIntegration.uploadFile(intricModel);
				final var fileEntity = fileRepository.save(toFileEntity(fileId));

				// Add file to session, update with success information
				sessionEntity.getFiles().add(fileEntity);
				sessionEntity.setCustomerNbr(intricModel.getCustomerNumber());
				sessionEntity.setStatus("Successfully initialized");
			} else {
				LOG.warn("No installed base information found for customer '{}' and counterparts {}", sessionRequest.getPartyId(), sessionRequest.getCustomerEngagementOrgIds());
				sessionEntity.setStatus("No installed base information found for customer '%s' and counterparts %s".formatted(sessionRequest.getPartyId(), sessionRequest.getCustomerEngagementOrgIds()));
			}

			sessionEntity.setInitialized(OffsetDateTime.now());

		} catch (final Exception e) {
			LOG.error("Exception thrown when populating session with customer information", e);
			// Update with failed information
			sessionEntity.setInitialized(null);
			sessionEntity.setStatus("Initialization failed, filter logs on log id '%s' for more information".formatted(RequestId.get()));
		} finally {
			sessionRepository.save(sessionEntity);
		}
	}

	private IntricModel buildIntricModel(final String municipalityId, final String partyId, final Map<String, InstalledBaseCustomer> installedBases) {
		final var intricModel = toIntricModel(installedBases);

		// Enrich all facility with agreement, invoice and measurement information
		final var facilities = ofNullable(intricModel.getFacilities()).orElse(emptyList());

		AgreementDecorator.addAgreements(facilities, agreementIntegration.getAgreements(municipalityId, partyId));
		InvoiceDecorator.addInvoices(facilities, invoicesIntegration.getInvoices(municipalityId, partyId));
		MeasurementDecorator.addMeasurements(facilities, measurementDataIntegration.getMeasurementData(municipalityId, partyId, facilities));

		return intricModel;
	}

	public boolean isSessionReady(String municipalityId, UUID sessionId) {
		return sessionRepository.existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(sessionId.toString(), municipalityId);
	}

	public QuestionResponse askQuestion(String municipalityId, UUID sessionId, String question) {
		final var session = sessionRepository.findBySessionIdAndMunicipalityId(sessionId.toString(), municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId)));

		if (isNull(session.getInitialized())) {
			return toQuestionResponse("Assistant is not ready yet");
		}

		final var intricResponse = intricIntegration.askFollowUp(intricProperties.assistantId(), session.getSessionId(), question, session.getFiles().stream().map(FileEntity::getFileId).toList());
		session.setLastAccessed(OffsetDateTime.now());
		sessionRepository.save(session);

		return intricResponse.map(AssistantMapper::toQuestionResponse).orElse(null);
	}

	@Async
	@Transactional
	public void deleteSessionById(final String municipalityId, final UUID sessionId, UUID requestId) {
		RequestId.init(ofNullable(requestId).orElse(UUID.randomUUID()).toString());

		sessionRepository.findBySessionIdAndMunicipalityId(sessionId.toString(), municipalityId)
			.ifPresentOrElse(entity -> Stream.of(entity)
				.map(this::saveChatHistory)
				.filter(Objects::nonNull)
				.findAny()
				.ifPresent(this::deleteSession), () -> {
					throw Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId));
				});
	}

	@Transactional
	public void cleanUpInactiveSessions(final Integer inactivityThreshold) {
		final var timestamp = OffsetDateTime.now().minusMinutes(inactivityThreshold);

		sessionRepository.findAllByLastAccessedBeforeOrLastAccessedIsNull(timestamp).stream()
			.filter(session -> isSubjectForRemoval(timestamp, session))
			.map(this::saveChatHistory)
			.filter(Objects::nonNull)
			.forEach(this::deleteSession);
	}

	/**
	 * To be subject for removal the session must either have a last accessed timestamp, or a
	 * created timestamp that is before the defined threshold for inactivity
	 *
	 * @param  timestamp timestamp when session is interpreted as inactive
	 * @param  session   session to evaluate
	 * @return           true if session is inactive and subject for removal, false otherwise
	 */
	private boolean isSubjectForRemoval(final OffsetDateTime timestamp, final SessionEntity session) {
		return Objects.nonNull(session.getLastAccessed()) || session.getCreated().isBefore(timestamp);
	}

	private SessionEntity saveChatHistory(final SessionEntity sessionEntity) {
		try {
			final var session = intricIntegration.getSession(intricProperties.assistantId(), sessionEntity.getSessionId());
			limeIntegration.saveChatHistory(sessionEntity.getPartyId(), sessionEntity.getCustomerNbr(), session);
			return sessionEntity;
		} catch (final Exception e) {
			LOG.error("Exception thrown when saving chat history for session", e);
			// Update with failed information
			sessionEntity.setStatus("Failed to save chat history, filter logs on log id '%s' for more information".formatted(RequestId.get()));
			return null;
		}
	}

	private void deleteSession(final SessionEntity sessionEntity) {
		sessionEntity.getFiles().removeIf(file -> {
			final var isRemoved = intricIntegration.deleteFile(file.getFileId());
			if (isRemoved) {
				fileRepository.delete(file);
			}
			return isRemoved;
		});

		if (sessionEntity.getFiles().isEmpty() && intricIntegration.deleteSession(intricProperties.assistantId(), sessionEntity.getSessionId())) {
			sessionRepository.delete(sessionEntity);
			return;
		}

		LOG.error("Could not delete session: {}", sessionEntity.getSessionId());
		sessionEntity.setStatus("Failed to delete session, filter logs on log id '%s' for more information".formatted(RequestId.get()));
	}
}
