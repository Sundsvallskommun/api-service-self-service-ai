package se.sundsvall.selfserviceai.integration.eneo.model.filecontent;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class EneoModelTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(EneoModel.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var customerNumber = "customerNumber";
		final var facilities = List.of(Facility.builder().build());
		final var partyId = "partyId";

		final var bean = EneoModel.builder()
			.withCustomerNumber(customerNumber)
			.withFacilities(facilities)
			.withPartyId(partyId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCustomerNumber()).isEqualTo(customerNumber);
		assertThat(bean.getFacilities()).isEqualTo(facilities);
		assertThat(bean.getPartyId()).isEqualTo(partyId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(EneoModel.builder().build())
			.hasAllNullFieldsOrPropertiesExcept("facilities")
			.hasFieldOrPropertyWithValue("facilities", emptyList());
		assertThat(new EneoModel())
			.hasAllNullFieldsOrPropertiesExcept("facilities")
			.hasFieldOrPropertyWithValue("facilities", emptyList());
	}
}
