package se.sundsvall.selfserviceai.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.OffsetDateTime;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReferenceTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(Reference.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var createdAt = OffsetDateTime.now().minusDays(7);
		final var groupId = "groupId";
		final var id = "id";
		final var metadata = Metadata.builder().build();
		final var score = 999;
		final var updatedAt = OffsetDateTime.now();
		final var websiteId = "websiteId";

		final var bean = Reference.builder()
			.withCreatedAt(createdAt)
			.withGroupId(groupId)
			.withId(id)
			.withMetadata(metadata)
			.withScore(score)
			.withUpdatedAt(updatedAt)
			.withWebsiteId(websiteId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCreatedAt()).isEqualTo(createdAt);
		assertThat(bean.getGroupId()).isEqualTo(groupId);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getMetadata()).isEqualTo(metadata);
		assertThat(bean.getScore()).isEqualTo(score);
		assertThat(bean.getUpdatedAt()).isEqualTo(updatedAt);
		assertThat(bean.getWebsiteId()).isEqualTo(websiteId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Reference.builder().build()).hasAllNullFieldsOrPropertiesExcept("score")
			.hasFieldOrPropertyWithValue("score", 0);
		assertThat(new Reference()).hasAllNullFieldsOrPropertiesExcept("score")
			.hasFieldOrPropertyWithValue("score", 0);
	}
}
