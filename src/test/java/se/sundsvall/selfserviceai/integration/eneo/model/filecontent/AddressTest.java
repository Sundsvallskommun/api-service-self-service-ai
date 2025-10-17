package se.sundsvall.selfserviceai.integration.eneo.model.filecontent;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class AddressTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Address.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethodsForAddress() {
		final var careOf = "careOf";
		final var city = "city";
		final var postalCode = "postalCode";
		final var street = "street";

		final var bean = Address.builder()
			.withCareOf(careOf)
			.withCity(city)
			.withPostalCode(postalCode)
			.withStreet(street)
			.build();

		assertThat(bean.getCareOf()).isEqualTo(careOf);
		assertThat(bean.getCity()).isEqualTo(city);
		assertThat(bean.getPostalCode()).isEqualTo(postalCode);
		assertThat(bean.getStreet()).isEqualTo(street);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Address.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Address()).hasAllNullFieldsOrProperties();
	}
}
