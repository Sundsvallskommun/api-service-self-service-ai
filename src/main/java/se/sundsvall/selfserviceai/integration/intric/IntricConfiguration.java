package se.sundsvall.selfserviceai.integration.intric;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

@Import(FeignConfiguration.class)
@EnableConfigurationProperties(IntricProperties.class)
class IntricConfiguration {

	public static final String CLIENT_ID = "intric";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final IntricProperties properties, final IntricTokenService tokenService) {
		return FeignMultiCustomizer.create()
			.withRequestInterceptor(template -> template.header(AUTHORIZATION, "Bearer " + tokenService.getToken()))
			.withRequestTimeoutsInSeconds(properties.connectTimeoutInSeconds(), properties.readTimeoutInSeconds())
			.composeCustomizersToOne();
	}
}
