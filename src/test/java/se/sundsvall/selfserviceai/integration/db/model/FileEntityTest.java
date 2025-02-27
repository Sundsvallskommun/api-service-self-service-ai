package se.sundsvall.selfserviceai.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class FileEntityTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(FileEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var fileId = "fileId";

		final var bean = FileEntity.builder()
			.withFileId(fileId)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getFileId()).isEqualTo(fileId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FileEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new FileEntity()).hasAllNullFieldsOrProperties();
	}

}
