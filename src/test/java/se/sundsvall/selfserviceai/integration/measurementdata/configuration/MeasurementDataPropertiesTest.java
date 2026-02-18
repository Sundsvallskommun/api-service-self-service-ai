package se.sundsvall.selfserviceai.integration.measurementdata.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.selfserviceai.Application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class MeasurementDataPropertiesTest {

	@Autowired
	private MeasurementDataProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeoutInSeconds()).isEqualTo(11);
		assertThat(properties.readTimeoutInSeconds()).isEqualTo(12);
	}
}
