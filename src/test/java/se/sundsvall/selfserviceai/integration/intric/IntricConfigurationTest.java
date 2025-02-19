package se.sundsvall.selfserviceai.integration.intric;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

@ExtendWith(MockitoExtension.class)
class IntricConfigurationTest {

	@Mock
	private IntricTokenService tokenServiceMock;

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Mock
	private FeignBuilderCustomizer feignBuilderCustomizerMock;

	@Mock
	private IntricProperties propertiesMock;

	@Test
	void testFeignBuilderCustomizer() {
		final var configuration = new IntricConfiguration();

		when(propertiesMock.connectTimeoutInSeconds()).thenReturn(1);
		when(propertiesMock.readTimeoutInSeconds()).thenReturn(2);
		when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(feignBuilderCustomizerMock);

		try (final MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			final var customizer = configuration.feignBuilderCustomizer(propertiesMock, tokenServiceMock);

			verify(propertiesMock).connectTimeoutInSeconds();
			verify(propertiesMock).readTimeoutInSeconds();
			verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(1, 2);
			verify(feignMultiCustomizerSpy).composeCustomizersToOne();

			assertThat(customizer).isSameAs(feignBuilderCustomizerMock);
		}
	}
}
