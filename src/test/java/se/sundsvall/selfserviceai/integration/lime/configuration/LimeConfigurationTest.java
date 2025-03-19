package se.sundsvall.selfserviceai.integration.lime.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import feign.RequestInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

@ExtendWith(MockitoExtension.class)
class LimeConfigurationTest {

	@Mock
	private ClientRegistrationRepository clientRegistrationRepositoryMock;

	@Mock
	private ClientRegistration clientRegistrationMock;

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Mock
	private FeignBuilderCustomizer feignBuilderCustomizerMock;

	@Mock
	private LimeProperties propertiesMock;

	@Test
	void testFeignBuilderCustomizer() {
		final var configuration = new LimeConfiguration();

		when(propertiesMock.connectTimeoutInSeconds()).thenReturn(1);
		when(propertiesMock.readTimeoutInSeconds()).thenReturn(2);
		when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(feignBuilderCustomizerMock);

		try (MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			final var customizer = configuration.feignBuilderCustomizer(propertiesMock);

			verify(propertiesMock).connectTimeoutInSeconds();
			verify(propertiesMock).readTimeoutInSeconds();
			verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(1, 2);
			verify(feignMultiCustomizerSpy).withRequestInterceptor(any(RequestInterceptor.class));
			verify(feignMultiCustomizerSpy).composeCustomizersToOne();

			assertThat(customizer).isSameAs(feignBuilderCustomizerMock);
		}
	}
}
