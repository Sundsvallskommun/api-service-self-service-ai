package se.sundsvall.selfserviceai.integration.intric;

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
		assertThat(properties.connectTimeoutInSeconds()).isEqualTo(5);
		assertThat(properties.readTimeoutInSeconds()).isEqualTo(15);
		assertThat(properties.oauth2()).satisfies(oauth -> {
			assertThat(oauth.tokenUrl()).isEqualTo("http://localhost:8080/intric/token");
			assertThat(oauth.username()).isEqualTo("override");
			assertThat(oauth.password()).isEqualTo("override");
		});
	}
}
