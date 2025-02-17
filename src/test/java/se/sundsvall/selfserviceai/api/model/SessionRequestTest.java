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

class SessionRequestTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(SessionRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var customerEngagementOrgId = "customerEngagementOrgId";
		final var partyId = "partyId";

		final var bean = SessionRequest.builder()
			.withCustomerEngagementOrgId(customerEngagementOrgId)
			.withPartyId(partyId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCustomerEngagementOrgId()).isEqualTo(customerEngagementOrgId);
		assertThat(bean.getPartyId()).isEqualTo(partyId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SessionRequest.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SessionRequest()).hasAllNullFieldsOrProperties();
	}
}
