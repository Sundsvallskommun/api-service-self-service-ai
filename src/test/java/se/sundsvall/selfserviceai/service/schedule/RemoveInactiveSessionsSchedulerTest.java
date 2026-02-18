package se.sundsvall.selfserviceai.service.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.sundsvall.selfserviceai.service.AssistantService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RemoveInactiveSessionsSchedulerTest {

	@Mock
	private AssistantService assistantServiceMock;

	@InjectMocks
	private RemoveInactiveSessionsScheduler removeInactiveSessionsScheduler;

	@Test
	void cleanUpInactiveSessions() {
		var inactivityThresholdInMinutes = 60;
		ReflectionTestUtils.setField(removeInactiveSessionsScheduler, "inactivityThresholdInMinutes", inactivityThresholdInMinutes);
		doNothing().when(assistantServiceMock).cleanUpInactiveSessions(inactivityThresholdInMinutes);

		removeInactiveSessionsScheduler.cleanUpInactiveSessions();

		verify(assistantServiceMock).cleanUpInactiveSessions(inactivityThresholdInMinutes);
		verifyNoMoreInteractions(assistantServiceMock);
	}

}
