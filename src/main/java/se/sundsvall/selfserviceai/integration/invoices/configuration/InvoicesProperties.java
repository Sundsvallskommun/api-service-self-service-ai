package se.sundsvall.selfserviceai.integration.invoices.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.invoices")
record InvoicesProperties(
	int connectTimeoutInSeconds,
	int readTimeoutInSeconds) {
}
