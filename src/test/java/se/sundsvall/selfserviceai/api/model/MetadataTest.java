package se.sundsvall.selfserviceai.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class MetadataTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Metadata.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var embeddingModelId = "embeddingModelId";
		final var size = 123456;
		final var title = "title";
		final var url = "url";

		final var bean = Metadata.builder()
			.withEmbeddingModelId(embeddingModelId)
			.withSize(size)
			.withTitle(title)
			.withUrl(url)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getEmbeddingModelId()).isEqualTo(embeddingModelId);
		assertThat(bean.getSize()).isEqualTo(size);
		assertThat(bean.getTitle()).isEqualTo(title);
		assertThat(bean.getUrl()).isEqualTo(url);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Metadata.builder().build()).hasAllNullFieldsOrPropertiesExcept("size")
			.hasFieldOrPropertyWithValue("size", 0);
		assertThat(new Metadata()).hasAllNullFieldsOrPropertiesExcept("size")
			.hasFieldOrPropertyWithValue("size", 0);
	}
}
