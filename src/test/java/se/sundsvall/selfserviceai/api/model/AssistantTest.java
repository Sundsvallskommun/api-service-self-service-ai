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

class AssistantTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Assistant.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var handle = "handle";
		final var id = "id";

		final var bean = Assistant.builder()
			.withHandle(handle)
			.withId(id)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getHandle()).isEqualTo(handle);
		assertThat(bean.getId()).isEqualTo(id);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Assistant.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Assistant()).hasAllNullFieldsOrProperties();
	}
}
