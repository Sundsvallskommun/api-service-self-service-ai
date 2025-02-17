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

class QuestionResponseTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(QuestionResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var answer = "answer";

		final var bean = QuestionResponse.builder()
			.withAnswer(answer)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAnswer()).isEqualTo(answer);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(QuestionResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new QuestionResponse()).hasAllNullFieldsOrProperties();
	}
}
