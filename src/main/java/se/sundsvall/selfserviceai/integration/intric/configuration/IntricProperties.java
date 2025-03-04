package se.sundsvall.selfserviceai.integration.intric.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.intric")
public record IntricProperties(

	@NotBlank String assistantId,

	@NotBlank String url,

	@Valid @NotNull Oauth2 oauth2,

	int connectTimeoutInSeconds,

	int readTimeoutInSeconds) {

	public record Oauth2(

		@NotBlank String url,
		@NotBlank String username,
		@NotBlank String password) {
	}
}
