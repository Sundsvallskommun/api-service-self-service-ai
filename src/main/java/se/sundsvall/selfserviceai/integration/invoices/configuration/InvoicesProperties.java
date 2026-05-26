package se.sundsvall.selfserviceai.integration.invoices.configuration;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.invoices")
public record InvoicesProperties(
	int connectTimeoutInSeconds,
	int readTimeoutInSeconds,
	@NotEmpty List<String> organizationNumbers) {
}
