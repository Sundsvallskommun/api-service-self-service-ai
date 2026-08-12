package se.sundsvall.selfserviceai.service.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.selfserviceai.service.AssistantService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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

	@Test
	void cleanUpInactiveSessionsPropagatesFailures() {
		var inactivityThresholdInMinutes = 60;
		final var exception = Problem.valueOf(INTERNAL_SERVER_ERROR, "1 of 2 inactive sessions could not be removed");
		ReflectionTestUtils.setField(removeInactiveSessionsScheduler, "inactivityThresholdInMinutes", inactivityThresholdInMinutes);
		doThrow(exception).when(assistantServiceMock).cleanUpInactiveSessions(inactivityThresholdInMinutes);

		// The failure must reach Dept44SchedulerAspect, as it is what sets the health indicator to unhealthy
		assertThat(assertThrows(ThrowableProblem.class, () -> removeInactiveSessionsScheduler.cleanUpInactiveSessions())).isSameAs(exception);

		verify(assistantServiceMock).cleanUpInactiveSessions(inactivityThresholdInMinutes);
		verifyNoMoreInteractions(assistantServiceMock);
	}

}
