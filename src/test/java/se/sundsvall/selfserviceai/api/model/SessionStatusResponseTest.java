package se.sundsvall.selfserviceai.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class SessionStatusResponseTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(SessionStatusResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var status = "READY";
		final var detail = "Some detail";

		final var bean = SessionStatusResponse.builder()
			.withStatus(status)
			.withDetail(detail)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getStatus()).isEqualTo(status);
		assertThat(bean.getDetail()).isEqualTo(detail);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SessionStatusResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SessionStatusResponse()).hasAllNullFieldsOrProperties();
	}
}
