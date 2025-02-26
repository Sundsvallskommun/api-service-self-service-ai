package se.sundsvall.selfserviceai.integration.intric.model.filecontent;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MeasurementDataTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(MeasurementData.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var category = "category";
		final var measurementType = "measurementType";
		final var timestamp = OffsetDateTime.now();
		final var unit = "unit";
		final var value = BigDecimal.valueOf(new Random().nextFloat());

		final var bean = MeasurementData.builder()
			.withCategory(category)
			.withMeasurementType(measurementType)
			.withTimestamp(timestamp)
			.withUnit(unit)
			.withValue(value)
			.build();

		assertThat(bean.getCategory()).isEqualTo(category);
		assertThat(bean.getMeasurementType()).isEqualTo(measurementType);
		assertThat(bean.getTimestamp()).isEqualTo(timestamp);
		assertThat(bean.getUnit()).isEqualTo(unit);
		assertThat(bean.getValue()).isEqualTo(value);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(MeasurementData.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new MeasurementData()).hasAllNullFieldsOrProperties();
	}
}
