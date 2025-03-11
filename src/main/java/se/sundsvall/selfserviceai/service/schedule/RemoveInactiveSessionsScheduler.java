package se.sundsvall.selfserviceai.service.schedule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.selfserviceai.service.AssistantService;

@Service
class RemoveInactiveSessionsScheduler {

	private final AssistantService assistantService;
	private final Integer inactivityThresholdInMinutes; // How many minutes a session can be inactive before it is removed

	RemoveInactiveSessionsScheduler(
		final AssistantService assistantService,
		@Value("${scheduler.remove-inactive-sessions.inactivity-threshold-in-minutes:60}") final Integer inactivityThresholdInMinutes) {
		this.assistantService = assistantService;
		this.inactivityThresholdInMinutes = inactivityThresholdInMinutes;
	}

	@Dept44Scheduled(
		cron = "${scheduler.remove-inactive-sessions.cron}",
		name = "${scheduler.remove-inactive-sessions.name}",
		lockAtMostFor = "${scheduler.remove-inactive-sessions.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.remove-inactive-sessions.maximum-execution-time}")
	void cleanUpInactiveSessions() {
		assistantService.cleanUpInactiveSessions(inactivityThresholdInMinutes);
	}

}
