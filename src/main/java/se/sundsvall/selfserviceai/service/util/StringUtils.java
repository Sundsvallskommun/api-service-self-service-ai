package se.sundsvall.selfserviceai.service.util;

import java.util.Collection;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

public final class StringUtils {
	private StringUtils() {}

	private static final String REGEXP_NON_PRINTABLE_ASCII_CHARACTERS = "[^\\x20-\\x7E]";
	private static final String REGEXP_POTENTIAL_HARMFUL_CHARACTERS = "[%\\\\]";
	private static final String REGEXP_MULTIPLE_SPACES = "(\s)\\1+";

	/**
	 * Helper method for sanitizing sent in string value by:
	 * - removing non-printable ASCII characters such as newlines and carriage returns etc
	 * - removing other potentially harmful characters
	 * - compressing multiple spaces in a row into one space
	 *
	 * @param  value string to be sanitized
	 * @return       a sanitized string based on sent in string
	 */
	public static String sanitizeAndCompress(String value) {
		return ofNullable(value)
			.map(s -> s.replaceAll(REGEXP_NON_PRINTABLE_ASCII_CHARACTERS, " "))
			.map(s -> s.replaceAll(REGEXP_POTENTIAL_HARMFUL_CHARACTERS, ""))
			.map(s -> s.replaceAll(REGEXP_MULTIPLE_SPACES, "$1"))
			.orElse(null);
	}

	public static Collection<String> sanitizeAndCompress(Collection<String> values) {
		return isNull(values) ? null
			: values.stream()
				.map(StringUtils::sanitizeAndCompress)
				.toList();
	}
}
