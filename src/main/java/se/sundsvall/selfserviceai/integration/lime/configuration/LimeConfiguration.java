package se.sundsvall.selfserviceai.integration.lime.configuration;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import feign.RequestInterceptor;
import java.util.List;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
public class LimeConfiguration {

	public static final String CLIENT_ID = "lime";

	private static final String API_KEY_HEADER_NAME = "X-Client-Secret";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final LimeProperties limeProperties, final ClientRegistrationRepository clientRegistrationRepository) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID, List.of(NOT_FOUND.value())))
			.withRequestTimeoutsInSeconds(limeProperties.connectTimeoutInSeconds(), limeProperties.readTimeoutInSeconds())
			.withRetryableOAuth2InterceptorForClientRegistration(clientRegistrationRepository.findByRegistrationId(CLIENT_ID))
			.withRequestInterceptor(requestInterceptor(limeProperties.apiKey()))
			.composeCustomizersToOne();
	}

	RequestInterceptor requestInterceptor(final String secret) {
		return requestTemplate -> ofNullable(secret).ifPresent(s -> requestTemplate.header(API_KEY_HEADER_NAME, s));
	}
}
