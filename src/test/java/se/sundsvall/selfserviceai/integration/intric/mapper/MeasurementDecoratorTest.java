package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;
import static se.sundsvall.selfserviceai.TestFactory.createMeasurements;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;

class MeasurementDecoratorTest {

	private static final IntricMapper INTRIC_MAPPER = new IntricMapper();

	@Test
	void addMeasurements() {
		final var installedBase = INTRIC_MAPPER.toIntricModel(Map.of("123456", createCustomer()));
		final var measurements = new ArrayList<>(createMeasurements(true));
		measurements.add(null);

		// Act
		MeasurementDecorator.addMeasurements(installedBase.getFacilities(), measurements);

		// Assert
		assertThat(installedBase.getFacilities()).satisfiesExactlyInAnyOrder(f -> {
			assertThat(f.getMeasurements()).hasSize(2).satisfiesExactlyInAnyOrder(m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_VALUE);
			}, m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_VALUE);
			});
		}, f -> {
			assertThat(f.getMeasurements()).hasSize(1).satisfiesExactlyInAnyOrder(m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_VALUE);
			});
		});
	}

	@Test
	void addMeasurementsNoMatches() {
		// Arrange
		final var installedBase = INTRIC_MAPPER.toIntricModel(Map.of("123456", createCustomer()));
		final var measurements = new ArrayList<>(createMeasurements(false));

		// Act
		MeasurementDecorator.addMeasurements(installedBase.getFacilities(), measurements);

		// Assert
		assertThat(installedBase.getFacilities()).flatExtracting(Facility::getMeasurements).isEmpty();
	}

	@Test
	void addMeasurementsFromNull() {
		// Arrange
		final var installedBase = INTRIC_MAPPER.toIntricModel(Map.of("123456", createCustomer()));

		// Act
		MeasurementDecorator.addMeasurements(installedBase.getFacilities(), null);

		// Assert
		assertThat(installedBase.getFacilities()).flatExtracting(Facility::getMeasurements).isEmpty();
	}
}
