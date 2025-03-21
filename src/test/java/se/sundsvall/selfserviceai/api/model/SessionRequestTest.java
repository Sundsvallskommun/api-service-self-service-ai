package se.sundsvall.selfserviceai.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
		final var customerEngagementOrgIds = Set.of("customerEngagementOrgId");
		final var partyId = "partyId";

		final var bean = SessionRequest.builder()
			.withCustomerEngagementOrgIds(customerEngagementOrgIds)
			.withPartyId(partyId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCustomerEngagementOrgIds()).isEqualTo(customerEngagementOrgIds);
		assertThat(bean.getPartyId()).isEqualTo(partyId);
	}

	@Test
	void testBuilderMethodsWithDuplicateOrgIds() {
		final var bean = SessionRequest.builder()
			.withCustomerEngagementOrgIds(List.of("customerEngagementOrgId", "customerEngagementOrgId")
				.stream()
				.collect(Collectors.toCollection(HashSet::new)))
			.build();

		assertThat(bean.getCustomerEngagementOrgIds()).hasSize(1).containsExactly("customerEngagementOrgId");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SessionRequest.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new SessionRequest()).hasAllNullFieldsOrProperties();
	}
}
