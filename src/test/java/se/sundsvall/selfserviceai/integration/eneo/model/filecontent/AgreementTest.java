package se.sundsvall.selfserviceai.integration.eneo.model.filecontent;

import java.time.LocalDate;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class AgreementTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(Agreement.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var agreementId = "agreementId";
		final var bindingRule = "bindingRule";
		final var bound = true;
		final var category = "category";
		final var description = "description";
		final var fromDate = LocalDate.now();

		final var bean = Agreement.builder()
			.withAgreementId(agreementId)
			.withBindingRule(bindingRule)
			.withBound(bound)
			.withCategory(category)
			.withDescription(description)
			.withFromDate(fromDate)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAgreementId()).isEqualTo(agreementId);
		assertThat(bean.getBindingRule()).isEqualTo(bindingRule);
		assertThat(bean.isBound()).isEqualTo(bound);
		assertThat(bean.getCategory()).isEqualTo(category);
		assertThat(bean.getDescription()).isEqualTo(description);
		assertThat(bean.getFromDate()).isEqualTo(fromDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Agreement.builder().build()).hasAllNullFieldsOrPropertiesExcept("bound").hasFieldOrPropertyWithValue("bound", false);
		assertThat(new Agreement()).hasAllNullFieldsOrPropertiesExcept("bound").hasFieldOrPropertyWithValue("bound", false);
	}
}
