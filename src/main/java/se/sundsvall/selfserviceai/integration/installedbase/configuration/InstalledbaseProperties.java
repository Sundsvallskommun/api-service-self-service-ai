package se.sundsvall.selfserviceai.integration.installedbase.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.installedbase")
record InstalledbaseProperties(
	int connectTimeoutInSeconds,
	int readTimeoutInSeconds) {
}
