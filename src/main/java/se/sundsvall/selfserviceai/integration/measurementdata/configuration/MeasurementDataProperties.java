package se.sundsvall.selfserviceai.integration.measurementdata.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.measurement-data")
public record MeasurementDataProperties(
	int connectTimeoutInSeconds,
	int readTimeoutInSeconds) {
}
