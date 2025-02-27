package se.sundsvall.selfserviceai.integration.intric.util;

import static java.time.ZoneId.systemDefault;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;

public class ConversionUtil {
	private ConversionUtil() {}

	public static boolean toBoolean(Boolean value) {
		return ofNullable(value)
			.orElse(false);
	}

	public static LocalDate toLocalDate(Date date) {
		return ofNullable(date)
			.map(d -> date.toInstant().atZone(systemDefault()).toLocalDate())
			.orElse(null);
	}

	public static OffsetDateTime toOffsetDateTime(Date date) {
		return ofNullable(date)
			.map(Date::toInstant)
			.map(d -> d.atOffset(OffsetDateTime.now(systemDefault()).getOffset()))
			.orElse(null);
	}

	public static BigDecimal toBigDecimal(Float value, int scale) {
		return ofNullable(value)
			.map(BigDecimal::valueOf)
			.map(bd -> bd.setScale(scale, RoundingMode.HALF_DOWN))
			.orElse(null);
	}
}
