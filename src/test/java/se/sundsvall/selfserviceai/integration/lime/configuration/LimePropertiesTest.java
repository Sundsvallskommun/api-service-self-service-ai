package se.sundsvall.selfserviceai.integration.lime.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.selfserviceai.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class LimePropertiesTest {

	@Autowired
	private LimeProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.apiKey()).isEqualTo("lime-api-key");
		assertThat(properties.connectTimeoutInSeconds()).isEqualTo(9);
		assertThat(properties.readTimeoutInSeconds()).isEqualTo(10);
	}
}
