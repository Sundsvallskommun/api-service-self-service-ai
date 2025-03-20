package se.sundsvall.selfserviceai.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class SessionResponseTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(SessionResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var assistantId = "assistantId";
		final var sessionId = "sessionId";

		final var bean = SessionResponse.builder()
			.withAssistantId(assistantId)
			.withSessionId(sessionId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAssistantId()).isEqualTo(assistantId);
		assertThat(bean.getSessionId()).isEqualTo(sessionId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SessionResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SessionResponse()).hasAllNullFieldsOrProperties();
	}
}
