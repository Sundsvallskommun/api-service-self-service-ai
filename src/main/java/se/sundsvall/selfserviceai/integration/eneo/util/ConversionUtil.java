package se.sundsvall.selfserviceai.integration.eneo.util;

import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversionUtil {
	private ConversionUtil() {}

	public static boolean toBoolean(final Boolean value) {
		return ofNullable(value)
			.orElse(false);
	}

	public static BigDecimal toBigDecimal(final Float value, final int scale) {
		return ofNullable(value)
			.map(BigDecimal::valueOf)
			.map(bigDecimal -> bigDecimal.setScale(scale, RoundingMode.HALF_DOWN))
			.orElse(null);
	}
}
