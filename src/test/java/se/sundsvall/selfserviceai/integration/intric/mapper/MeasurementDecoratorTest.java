package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;
import static se.sundsvall.selfserviceai.TestFactory.createMeasurements;

import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;

class MeasurementDecoratorTest {

	@Test
	void addMeasurements() {
		// Arrange
		final var scale = 4;
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var measurements = new ArrayList<>(createMeasurements(true));
		measurements.add(null);

		// Act
		final var result = MeasurementDecorator.addMeasurements(installedBase, measurements);

		// Assert
		assertThat(result.getFacilities()).satisfiesExactlyInAnyOrder(f -> {
			assertThat(f.getMeasurements()).hasSize(2).satisfiesExactlyInAnyOrder(m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_VALUE.setScale(scale, RoundingMode.HALF_DOWN));
			}, m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_VALUE.setScale(scale, RoundingMode.HALF_DOWN));
			});
		}, f -> {
			assertThat(f.getMeasurements()).hasSize(1).satisfiesExactlyInAnyOrder(m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_VALUE.setScale(scale, RoundingMode.HALF_DOWN));
			});
		});
	}

	@Test
	void addMeasurementsNoMatches() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var measurements = new ArrayList<>(createMeasurements(false));

		// Act
		final var result = MeasurementDecorator.addMeasurements(installedBase, measurements);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getMeasurements).isEmpty();
	}

	@Test
	void addMeasurementsFromNull() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());

		// Act
		final var result = MeasurementDecorator.addMeasurements(installedBase, null);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getMeasurements).isEmpty();
	}
}
