package se.sundsvall.selfserviceai.integration.lime.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.lime")
record LimeProperties(
	int connectTimeoutInSeconds,
	int readTimeoutInSeconds,
	String apiKey) {
}
