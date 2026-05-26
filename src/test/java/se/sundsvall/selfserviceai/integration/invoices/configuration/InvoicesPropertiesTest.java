package se.sundsvall.selfserviceai.integration.invoices.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.selfserviceai.Application;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class InvoicesPropertiesTest {

	@Autowired
	private InvoicesProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeoutInSeconds()).isEqualTo(7);
		assertThat(properties.readTimeoutInSeconds()).isEqualTo(8);
		assertThat(properties.organizationNumbers()).containsExactly("junit-org-1", "junit-org-2");
	}
}
