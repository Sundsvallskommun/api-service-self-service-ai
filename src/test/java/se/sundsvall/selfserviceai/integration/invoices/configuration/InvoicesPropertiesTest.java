package se.sundsvall.selfserviceai.integration.invoices.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.selfserviceai.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class InvoicesPropertiesTest {

	@Autowired
	private InvoicesProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeoutInSeconds()).isEqualTo(7);
		assertThat(properties.readTimeoutInSeconds()).isEqualTo(8);
	}
}
