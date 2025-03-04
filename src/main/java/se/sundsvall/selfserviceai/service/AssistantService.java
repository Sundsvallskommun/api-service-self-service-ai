package se.sundsvall.selfserviceai.service;

import static java.util.Objects.isNull;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.selfserviceai.integration.db.DatabaseMapper.toFileEntity;
import static se.sundsvall.selfserviceai.integration.db.DatabaseMapper.toSessionEntity;
import static se.sundsvall.selfserviceai.service.AssistantMapper.toQuestionResponse;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.api.model.SessionRequest;
import se.sundsvall.selfserviceai.integration.db.FileRepository;
import se.sundsvall.selfserviceai.integration.db.SessionRepository;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;
import se.sundsvall.selfserviceai.integration.intric.IntricIntegration;
import se.sundsvall.selfserviceai.integration.intric.configuration.IntricProperties;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;

@Service
public class AssistantService {

	private static final Logger LOG = LoggerFactory.getLogger(AssistantService.class);
	private static final String ERROR_SESSION_NOT_FOUND = "Session with id '%s' could not be found";

	private final IntricIntegration intricIntegration;
	private final IntricProperties intricProperties;
	private final SessionRepository sessionRepository;
	private final FileRepository fileRepository;

	public AssistantService(IntricProperties intricProperties, IntricIntegration intricIntegration, SessionRepository sessionRepository, FileRepository fileRepository) {
		this.intricProperties = intricProperties;
		this.intricIntegration = intricIntegration;
		this.sessionRepository = sessionRepository;
		this.fileRepository = fileRepository;
	}

	public UUID createSession(String municipalityId) {
		final var session = intricIntegration.askAssistant(intricProperties.assistantId(), "Påbörjar session");
		sessionRepository.save(toSessionEntity(municipalityId, session.sessionId()));

		return session.sessionId();
	}

	@Async
	@Transactional
	public void populateWithInformation(UUID sessionId, SessionRequest sessionRequest) {
		final var session = sessionRepository.findById(sessionId.toString())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId)));

		try {
			final var fileId = intricIntegration.uploadFile(InstalledBase.builder().withPartyId(sessionRequest.getPartyId()).build()); // TODO: Add real data here in later task

			final var file = fileRepository.save(toFileEntity(fileId));

			session.getFiles().add(file);
			session.setInitialized(OffsetDateTime.now());
			session.setInitiationStatus("Successfully initialized");
		} catch (final Exception e) {
			LOG.error("Exception thrown when populating session with customer information", e);
			session.setInitiationStatus("Initialization failed");
		} finally {
			sessionRepository.save(session);
		}
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
	public void deleteSession(String municipalityId, UUID sessionId) {
		final var session = sessionRepository.findBySessionIdAndMunicipalityId(sessionId.toString(), municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERROR_SESSION_NOT_FOUND.formatted(sessionId)));

		session.getFiles().removeIf(file -> {
			final var isRemoved = intricIntegration.deleteFile(file.getFileId());
			if (isRemoved) {
				fileRepository.delete(file);
			}
			return isRemoved;
		});

		if (session.getFiles().isEmpty() && intricIntegration.deleteSession(intricProperties.assistantId(), session.getSessionId())) {
			sessionRepository.delete(session);
		}
	}
}
