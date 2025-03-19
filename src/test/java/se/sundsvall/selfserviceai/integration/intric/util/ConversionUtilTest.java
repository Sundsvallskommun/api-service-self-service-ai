package se.sundsvall.selfserviceai.integration.intric.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConversionUtilTest {

	@Test
	void toBoolean() {
		assertThat(ConversionUtil.toBoolean(Boolean.TRUE)).isTrue();
		assertThat(ConversionUtil.toBoolean(Boolean.FALSE)).isFalse();
	}

	@Test
	void toBooleanFromNull() {
		assertThat(ConversionUtil.toBoolean(null)).isFalse();
	}

	@ParameterizedTest
	@ValueSource(ints = {
		0, 1, 2, 3, 4, 5
	})
	void toBigDecimal(int scale) {
		final var result = ConversionUtil.toBigDecimal(123.45678f, scale);

		switch (scale) {
			case 0 -> assertThat(result).isEqualTo(new BigDecimal("123"));
			case 1 -> assertThat(result).isEqualTo(new BigDecimal("123.5"));
			case 2 -> assertThat(result).isEqualTo(new BigDecimal("123.46"));
			case 3 -> assertThat(result).isEqualTo(new BigDecimal("123.457"));
			case 4 -> assertThat(result).isEqualTo(new BigDecimal("123.4568"));
			case 5 -> assertThat(result).isEqualTo(new BigDecimal("123.45678"));
			default -> throw new RuntimeException("Scale value %s is not handled".formatted(scale));
		}
	}

	@Test
	void toBigDecimalFromNull() {
		assertThat(ConversionUtil.toBigDecimal(null, 0)).isNull();
	}
}
