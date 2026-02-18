package se.sundsvall.selfserviceai.integration.db.model;

import java.time.OffsetDateTime;
import java.util.List;
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
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.CoreMatchers.allOf;

class SessionEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(SessionEntity.class, allOf(
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
		final var files = List.of(FileEntity.builder().build());
		final var initialized = OffsetDateTime.now().minusMinutes(59);
		final var lastAccessed = OffsetDateTime.now().minusMinutes(55);
		final var municipalityId = "municipalityId";
		final var partyId = "partyId";
		final var sessionId = "sessionId";
		final var status = "status";

		final var bean = SessionEntity.builder()
			.withCreated(created)
			.withCustomerNbr(customerNbr)
			.withFiles(files)
			.withInitialized(initialized)
			.withLastAccessed(lastAccessed)
			.withMunicipalityId(municipalityId)
			.withPartyId(partyId)
			.withSessionId(sessionId)
			.withStatus(status)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getCustomerNbr()).isEqualTo(customerNbr);
		assertThat(bean.getFiles()).isEqualTo(files);
		assertThat(bean.getInitialized()).isEqualTo(initialized);
		assertThat(bean.getLastAccessed()).isEqualTo(lastAccessed);
		assertThat(bean.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(bean.getPartyId()).isEqualTo(partyId);
		assertThat(bean.getSessionId()).isEqualTo(sessionId);
		assertThat(bean.getStatus()).isEqualTo(status);
	}

	@Test
	void testPrePersist() {
		final var bean = SessionEntity.builder().build();

		assertThat(bean.getCreated()).isNull();

		bean.prePersist();

		assertThat(bean.getCreated()).isCloseTo(OffsetDateTime.now(), within(30, MILLIS));
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SessionEntity.builder().build())
			.hasAllNullFieldsOrPropertiesExcept("files")
			.hasFieldOrPropertyWithValue("files", emptyList());
		assertThat(new SessionEntity())
			.hasAllNullFieldsOrPropertiesExcept("files")
			.hasFieldOrPropertyWithValue("files", emptyList());
	}

}
