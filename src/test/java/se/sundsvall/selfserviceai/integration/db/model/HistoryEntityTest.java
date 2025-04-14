package se.sundsvall.selfserviceai.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.OffsetDateTime;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HistoryEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(HistoryEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var created = OffsetDateTime.now().minusHours(1);
		final var customerNbr = "customerNbr";
		final var limeHistory = "limeHistory";
		final var partyId = "partyId";
		final var sessionId = "sessionId";

		final var bean = HistoryEntity.builder()
			.withCreated(created)
			.withCustomerNbr(customerNbr)
			.withLimeHistory(limeHistory)
			.withPartyId(partyId)
			.withSessionId(sessionId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getCustomerNbr()).isEqualTo(customerNbr);
		assertThat(bean.getLimeHistory()).isEqualTo(limeHistory);
		assertThat(bean.getPartyId()).isEqualTo(partyId);
		assertThat(bean.getSessionId()).isEqualTo(sessionId);
	}

	@Test
	void testPrePersist() {
		final var bean = HistoryEntity.builder().build();

		assertThat(bean.getCreated()).isNull();

		bean.prePersist();

		assertThat(bean.getCreated()).isCloseTo(OffsetDateTime.now(), within(30, MILLIS));
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(HistoryEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new HistoryEntity()).hasAllNullFieldsOrProperties();
	}

}
