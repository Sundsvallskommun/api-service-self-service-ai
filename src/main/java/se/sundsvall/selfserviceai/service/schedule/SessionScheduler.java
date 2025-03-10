package se.sundsvall.selfserviceai.service.schedule;

import org.springframework.stereotype.Service;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.selfserviceai.service.AssistantService;

@Service
class SessionScheduler {

	private final AssistantService assistantService;

	SessionScheduler(final AssistantService assistantService) {
		this.assistantService = assistantService;
	}

	@Dept44Scheduled(
		cron = "${scheduler.session.cron}",
		name = "${scheduler.session.name}",
		lockAtMostFor = "${scheduler.session.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.session.maximum-execution-time}")
	void cleanUpExpiredSessions() {
		assistantService.cleanUpExpiredSessions();
	}

}
