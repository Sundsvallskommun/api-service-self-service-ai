package se.sundsvall.selfserviceai.api.model;

import java.time.OffsetDateTime;
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

class FileTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(File.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var createdAt = OffsetDateTime.now().minusDays(7);
		final var id = "id";
		final var mimeType = "mimeType";
		final var name = "name";
		final var size = 123456;
		final var transcription = "transcription";
		final var updatedAt = OffsetDateTime.now();

		final var bean = File.builder()
			.withCreatedAt(createdAt)
			.withId(id)
			.withMimeType(mimeType)
			.withName(name)
			.withSize(size)
			.withTranscription(transcription)
			.withUpdatedAt(updatedAt)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCreatedAt()).isEqualTo(createdAt);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getMimeType()).isEqualTo(mimeType);
		assertThat(bean.getName()).isEqualTo(name);
		assertThat(bean.getSize()).isEqualTo(size);
		assertThat(bean.getTranscription()).isEqualTo(transcription);
		assertThat(bean.getUpdatedAt()).isEqualTo(updatedAt);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(File.builder().build()).hasAllNullFieldsOrPropertiesExcept("size").hasFieldOrPropertyWithValue("size", 0);
		assertThat(new File()).hasAllNullFieldsOrPropertiesExcept("size").hasFieldOrPropertyWithValue("size", 0);
	}
}
