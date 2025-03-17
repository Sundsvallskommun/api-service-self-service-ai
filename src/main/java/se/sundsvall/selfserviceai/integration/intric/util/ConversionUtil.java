package se.sundsvall.selfserviceai.integration.intric.util;

import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversionUtil {
	private ConversionUtil() {}

	public static boolean toBoolean(Boolean value) {
		return ofNullable(value)
			.orElse(false);
	}

	public static BigDecimal toBigDecimal(Float value, int scale) {
		return ofNullable(value)
			.map(BigDecimal::valueOf)
			.map(bd -> bd.setScale(scale, RoundingMode.HALF_DOWN))
			.orElse(null);
	}
}
