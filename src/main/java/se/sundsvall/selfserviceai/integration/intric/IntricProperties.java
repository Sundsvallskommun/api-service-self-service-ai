package se.sundsvall.selfserviceai.integration.intric;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.intric")
record IntricProperties(

	@NotBlank String baseUrl,

	String apiKey,

	@Valid @NotNull Oauth2 oauth2,

	@DefaultValue("120") int connectTimeoutInSeconds,

	@DefaultValue("120") int readTimeoutInSeconds) {

	public record Oauth2(

		@NotBlank String tokenUrl,
		@NotBlank String username,
		@NotBlank String password) {
	}
}
