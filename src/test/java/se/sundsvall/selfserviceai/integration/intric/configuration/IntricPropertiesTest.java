package se.sundsvall.selfserviceai.integration.intric.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.selfserviceai.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class IntricPropertiesTest {

	@Autowired
	private IntricProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.assistantId()).isEqualTo("intric-assistant-uuid");
		assertThat(properties.url()).isEqualTo("intric-integration-url");
		assertThat(properties.connectTimeoutInSeconds()).isEqualTo(9);
		assertThat(properties.readTimeoutInSeconds()).isEqualTo(10);
		assertThat(properties.oauth2()).satisfies(oauth -> {
			assertThat(oauth.url()).isEqualTo("intric-token-url");
			assertThat(oauth.username()).isEqualTo("override");
			assertThat(oauth.password()).isEqualTo("override");
		});
	}
}
