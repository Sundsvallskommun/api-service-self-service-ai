package se.sundsvall.selfserviceai.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.api.model.SessionRequest;

@Service
public class AssistantService {
	private final Map<UUID, Boolean> sessionStatus = new HashMap<>(); // Fake "database" for keeping check on active/inactive sessions. Will be replaced with "real" code later.

	public UUID createSession(String municipalityId) {
		final var sessionId = UUID.randomUUID();
		sessionStatus.put(sessionId, false);

		return sessionId;
	}

	@Async
	public void populateWithInformation(UUID sessionId, SessionRequest sessionRequest) {
		try {
			Thread.sleep(Duration.ofSeconds(3l));
		} catch (final InterruptedException e) {} finally {
			sessionStatus.put(sessionId, true);
		}
	}

	public boolean isSessionReady(String municipalityId, UUID sessionId) {
		return sessionStatus.entrySet().stream()
			.filter(entry -> entry.getKey().equals(sessionId))
			.map(Entry::getValue)
			.findAny()
			.orElse(false);
	}

	public QuestionResponse askQuestion(String municipalityId, UUID sessionId, String question) {
		return sessionStatus.entrySet().stream()
			.filter(entry -> entry.getKey().equals(sessionId))
			.filter(entry -> entry.getValue().equals(true))
			.findAny()
			.map(entry -> QuestionResponse.builder().withAnswer("42").build())
			.orElse(QuestionResponse.builder().withAnswer("Assistant is not ready yet").build());
	}

	@Async
	public void deleteSession(String municipalityId, UUID sessionId) {
		sessionStatus.entrySet().removeIf(entry -> entry.getKey().equals(sessionId));
	}
}
