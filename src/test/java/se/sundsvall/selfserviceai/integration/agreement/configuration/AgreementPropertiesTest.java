package se.sundsvall.selfserviceai.integration.agreement.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.selfserviceai.Application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class AgreementPropertiesTest {

	@Autowired
	private AgreementProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeoutInSeconds()).isEqualTo(1);
		assertThat(properties.readTimeoutInSeconds()).isEqualTo(2);
	}
}
