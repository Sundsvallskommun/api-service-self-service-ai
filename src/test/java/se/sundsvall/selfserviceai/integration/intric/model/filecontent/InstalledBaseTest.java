package se.sundsvall.selfserviceai.integration.intric.model.filecontent;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InstalledBaseTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(InstalledBase.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));

		MatcherAssert.assertThat(InstalledBase.Metadata.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethodsForCommitment() {
		final var commitmentEndDate = LocalDate.now().plusDays(1);
		final var commitmentStartDate = LocalDate.now().minusDays(1);
		final var information = List.of(InstalledBase.Metadata.builder().build());
		final var lastModifiedDate = LocalDate.now();
		final var placementId = 123;
		final var type = "type";

		final var bean = InstalledBase.builder()
			.withCommitmentEndDate(commitmentEndDate)
			.withCommitmentStartDate(commitmentStartDate)
			.withInformation(information)
			.withLastModifiedDate(lastModifiedDate)
			.withPlacementId(placementId)
			.withType(type)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCommitmentEndDate()).isEqualTo(commitmentEndDate);
		assertThat(bean.getCommitmentStartDate()).isEqualTo(commitmentStartDate);
		assertThat(bean.getInformation()).isEqualTo(information);
		assertThat(bean.getLastModifiedDate()).isEqualTo(lastModifiedDate);
		assertThat(bean.getPlacementId()).isEqualTo(placementId);
		assertThat(bean.getType()).isEqualTo(type);
	}

	void testBuilderMethodsForMetadata() {
		final var displayName = "displayName";
		final var name = "name";
		final var type = "type";
		final var value = "value";

		final var bean = InstalledBase.Metadata.builder()
			.withDisplayName(displayName)
			.withName(name)
			.withType(type)
			.withValue(value)
			.build();

		assertThat(bean.getDisplayName()).isEqualTo(displayName);
		assertThat(bean.getName()).isEqualTo(name);
		assertThat(bean.getType()).isEqualTo(type);
		assertThat(bean.getValue()).isEqualTo(value);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(InstalledBase.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new InstalledBase()).hasAllNullFieldsOrProperties();

		assertThat(InstalledBase.Metadata.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new InstalledBase.Metadata()).hasAllNullFieldsOrProperties();
	}
}
