package se.sundsvall.selfserviceai.integration.agreement.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.agreement")
record AgreementProperties(
	int connectTimeoutInSeconds,
	int readTimeoutInSeconds) {
}
