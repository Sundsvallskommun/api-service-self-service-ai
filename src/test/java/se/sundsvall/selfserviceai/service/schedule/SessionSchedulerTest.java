package se.sundsvall.selfserviceai.service.schedule;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.selfserviceai.service.AssistantService;

@ExtendWith(MockitoExtension.class)
class SessionSchedulerTest {

	@Mock
	private AssistantService assistantServiceMock;

	@InjectMocks
	private SessionScheduler sessionScheduler;

	@Test
	void cleanUpExpiredSessions() {
		doNothing().when(assistantServiceMock).cleanUpExpiredSessions();

		sessionScheduler.cleanUpExpiredSessions();

		verify(assistantServiceMock).cleanUpExpiredSessions();
		verifyNoMoreInteractions(assistantServiceMock);
	}

}
