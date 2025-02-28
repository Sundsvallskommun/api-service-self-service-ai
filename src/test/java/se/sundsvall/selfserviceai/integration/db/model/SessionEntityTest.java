package se.sundsvall.selfserviceai.integration.db.model;

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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
		final var files = List.of(FileEntity.builder().build());
		final var initialized = OffsetDateTime.now().minusMinutes(59);
		final var initiationStatus = "initiationStatus";
		final var lastAccessed = OffsetDateTime.now().minusMinutes(55);
		final var municipalityId = "municipalityId";
		final var sessionId = "sessionId";

		final var bean = SessionEntity.builder()
			.withCreated(created)
			.withFiles(files)
			.withInitialized(initialized)
			.withInitiationStatus(initiationStatus)
			.withLastAccessed(lastAccessed)
			.withMunicipalityId(municipalityId)
			.withSessionId(sessionId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getFiles()).isEqualTo(files);
		assertThat(bean.getInitialized()).isEqualTo(initialized);
		assertThat(bean.getInitiationStatus()).isEqualTo(initiationStatus);
		assertThat(bean.getLastAccessed()).isEqualTo(lastAccessed);
		assertThat(bean.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(bean.getSessionId()).isEqualTo(sessionId);
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
