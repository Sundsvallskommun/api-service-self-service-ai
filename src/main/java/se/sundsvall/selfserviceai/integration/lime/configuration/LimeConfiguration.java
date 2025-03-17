package se.sundsvall.selfserviceai.integration.lime.configuration;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

@Import(FeignConfiguration.class)
public class LimeConfiguration {

	public static final String CLIENT_ID = "lime";

	private static final String API_KEY_HEADER_NAME = "X-Client-Secret";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final LimeProperties limeProperties) {
		return FeignMultiCustomizer.create()
			.withRequestTimeoutsInSeconds(limeProperties.connectTimeoutInSeconds(), limeProperties.readTimeoutInSeconds())
			.withRequestInterceptor(requestInterceptor(limeProperties.apiKey()))
			.composeCustomizersToOne();
	}

	RequestInterceptor requestInterceptor(final String apiKey) {
		return requestTemplate -> requestTemplate.header(API_KEY_HEADER_NAME, apiKey);
	}
}
