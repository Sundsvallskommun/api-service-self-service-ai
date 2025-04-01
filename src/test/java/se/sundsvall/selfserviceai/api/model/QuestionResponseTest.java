package se.sundsvall.selfserviceai.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.List;
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
		final var files = List.of(File.builder().build());
		final var model = Model.builder().build();
		final var question = "question";
		final var references = List.of(Reference.builder().build());
		final var sessionId = "sessionId";
		final var tools = Tools.builder().build();

		final var bean = QuestionResponse.builder()
			.withAnswer(answer)
			.withFiles(files)
			.withModel(model)
			.withQuestion(question)
			.withReferences(references)
			.withSessionId(sessionId)
			.withTools(tools)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAnswer()).isEqualTo(answer);
		assertThat(bean.getFiles()).isEqualTo(files);
		assertThat(bean.getModel()).isEqualTo(model);
		assertThat(bean.getQuestion()).isEqualTo(question);
		assertThat(bean.getReferences()).isEqualTo(references);
		assertThat(bean.getSessionId()).isEqualTo(sessionId);
		assertThat(bean.getTools()).isEqualTo(tools);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(QuestionResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new QuestionResponse()).hasAllNullFieldsOrProperties();
	}
}
