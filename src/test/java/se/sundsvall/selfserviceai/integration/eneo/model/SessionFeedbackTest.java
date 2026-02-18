package se.sundsvall.selfserviceai.integration.eneo.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionFeedbackTest {

	private static final int VALUE = 1;
	private static final String TEXT = "text";

	@Test
	void builderPatternTest() {
		var bean = SessionFeedback.builder()
			.withText(TEXT)
			.withValue(VALUE)
			.build();

		assertThat(bean.text()).isEqualTo(TEXT);
		assertThat(bean.value()).isEqualTo(VALUE);
		assertThat(bean).hasOnlyFields("text", "value");
	}

	@Test
	void constructorTest() {
		var bean = new SessionFeedback(VALUE, TEXT);

		assertThat(bean.text()).isEqualTo(TEXT);
		assertThat(bean.value()).isEqualTo(VALUE);
		assertThat(bean).hasOnlyFields("text", "value");
	}

	@Test
	void noDirtOnCreatedBean() {
		var bean = SessionFeedback.builder().build();
		assertThat(bean).hasAllNullFieldsOrPropertiesExcept("value");
		assertThat(bean.value()).isEqualTo(0);
	}

}
