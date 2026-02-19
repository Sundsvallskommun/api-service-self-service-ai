package se.sundsvall.selfserviceai.integration.eneo.mapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;
import static se.sundsvall.selfserviceai.TestFactory.createMeasurements;

class MeasurementDecoratorTest {

	private static final EneoMapper ENEO_MAPPER = new EneoMapper();

	@Test
	void addMeasurements() {
		final var installedBase = ENEO_MAPPER.toEneoModel(Map.of("123456", createCustomer()));
		final var measurements = new ArrayList<>(createMeasurements(true));
		measurements.add(null);

		// Act
		MeasurementDecorator.addMeasurements(installedBase.getFacilities(), measurements);

		// Assert
		assertThat(installedBase.getFacilities()).satisfiesExactlyInAnyOrder(facility -> {
			assertThat(facility.getMeasurements()).hasSize(2).satisfiesExactlyInAnyOrder(measurementData -> {
				assertThat(measurementData.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_CATEGORY.name());
				assertThat(measurementData.getMeasurementType()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_TYPE);
				assertThat(measurementData.getTimestamp()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(measurementData.getUnit()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_UNIT);
				assertThat(measurementData.getValue()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_VALUE);
			}, measurementData -> {
				assertThat(measurementData.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_CATEGORY.name());
				assertThat(measurementData.getMeasurementType()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_TYPE);
				assertThat(measurementData.getTimestamp()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(measurementData.getUnit()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_UNIT);
				assertThat(measurementData.getValue()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_VALUE);
			});
		}, facility -> assertThat(facility.getMeasurements()).hasSize(1).satisfiesExactlyInAnyOrder(measurementData -> {
			assertThat(measurementData.getCategory()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_CATEGORY.name());
			assertThat(measurementData.getMeasurementType()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_TYPE);
			assertThat(measurementData.getTimestamp()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
			assertThat(measurementData.getUnit()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_UNIT);
			assertThat(measurementData.getValue()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_VALUE);
		}));
	}

	@Test
	void addMeasurementsNoMatches() {
		// Arrange
		final var installedBase = ENEO_MAPPER.toEneoModel(Map.of("123456", createCustomer()));
		final var measurements = new ArrayList<>(createMeasurements(false));

		// Act
		MeasurementDecorator.addMeasurements(installedBase.getFacilities(), measurements);

		// Assert
		assertThat(installedBase.getFacilities()).flatExtracting(Facility::getMeasurements).isEmpty();
	}

	@Test
	void addMeasurementsFromNull() {
		// Arrange
		final var installedBase = ENEO_MAPPER.toEneoModel(Map.of("123456", createCustomer()));

		// Act
		MeasurementDecorator.addMeasurements(installedBase.getFacilities(), null);

		// Assert
		assertThat(installedBase.getFacilities()).flatExtracting(Facility::getMeasurements).isEmpty();
	}
}
