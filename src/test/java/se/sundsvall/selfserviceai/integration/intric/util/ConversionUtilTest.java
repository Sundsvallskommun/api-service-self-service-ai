package se.sundsvall.selfserviceai.integration.intric.util;

import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
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

	@Test
	void toLocalDate() {
		final var cal = Calendar.getInstance();
		cal.set(1970, 0, 1); // Calendar has 0-based monthvalues

		assertThat(ConversionUtil.toLocalDate(cal.getTime())).isEqualTo(LocalDate.of(1970, 1, 1));
		assertThat(ConversionUtil.toLocalDate(new Date())).isEqualTo(LocalDate.now());
	}

	@Test
	void toLocalDateFromNull() {
		assertThat(ConversionUtil.toLocalDate(null)).isNull();
	}

	@Test
	void toOffsetDateTime() {
		final var cal = Calendar.getInstance();
		cal.set(1970, 0, 1); // Calendar has 0-based monthvalues

		assertThat(ConversionUtil.toOffsetDateTime(cal.getTime())).isCloseTo(OffsetDateTime.ofInstant(cal.toInstant(), systemDefault()), within(50, MILLIS));
		assertThat(ConversionUtil.toOffsetDateTime(new Date())).isCloseTo(OffsetDateTime.now(), within(50, MILLIS));
	}

	@Test
	void toOffsetDateTimeFromNull() {
		assertThat(ConversionUtil.toOffsetDateTime(null)).isNull();
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
		}
	}

	@Test
	void toBigDecimalFromNull() {
		assertThat(ConversionUtil.toBigDecimal(null, 0)).isNull();
	}
}
