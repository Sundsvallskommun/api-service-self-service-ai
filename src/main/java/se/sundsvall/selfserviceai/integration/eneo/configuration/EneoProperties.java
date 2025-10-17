package se.sundsvall.selfserviceai.integration.eneo.configuration;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.eneo")
public record EneoProperties(
	@DefaultValue("30") int connectTimeoutInSeconds,
	@DefaultValue("5") int readTimeoutInSeconds,
	@NotBlank String assistantId,
	@NotBlank String apiKey) {
}
