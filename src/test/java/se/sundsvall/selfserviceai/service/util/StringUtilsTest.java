package se.sundsvall.selfserviceai.service.util;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

	@Test
	void sanitizeAndCompress() {
		assertThat(StringUtils.sanitizeAndCompress((String) null)).isNull();
		assertThat(StringUtils.sanitizeAndCompress("Lorem Ipsum")).isEqualTo("Lorem Ipsum");
		assertThat(StringUtils.sanitizeAndCompress("Lorem\n\r\n\r\nIpsum")).isEqualTo("Lorem Ipsum");
		assertThat(StringUtils.sanitizeAndCompress("Lorem\n\rIpsum")).isEqualTo("Lorem Ipsum");
		assertThat(StringUtils.sanitizeAndCompress("Lorem\bIpsum")).isEqualTo("Lorem Ipsum");
		assertThat(StringUtils.sanitizeAndCompress("Lo%rem Ip\\\\\\\\sum")).isEqualTo("Lorem Ipsum");
		assertThat(StringUtils.sanitizeAndCompress("%Lorem\n\rIpsum")).isEqualTo("Lorem Ipsum");
		assertThat(StringUtils.sanitizeAndCompress("Lorem\nIpsum\nAbbacus")).isEqualTo("Lorem Ipsum Abbacus");
		assertThat(StringUtils.sanitizeAndCompress("Lorem\rIpsum\rAbbacus")).isEqualTo("Lorem Ipsum Abbacus");
	}

	@SuppressWarnings({
		"unchecked", "rawtypes"
	})
	@Test
	void sanitizeAndCompressForCollection() {
		assertThat(StringUtils.sanitizeAndCompress((Collection) null)).isNull();
		assertThat(StringUtils.sanitizeAndCompress(List.of("Lorem Ipsum", "Neque Porro Quisquam"))).isEqualTo(List.of("Lorem Ipsum", "Neque Porro Quisquam"));
		assertThat(StringUtils.sanitizeAndCompress(List.of("Lorem\n\r\n\r\nIpsum", "Neque\n\r\n\r\nPorro\n\rQuisquam"))).isEqualTo(List.of("Lorem Ipsum", "Neque Porro Quisquam"));
		assertThat(StringUtils.sanitizeAndCompress(List.of("Lorem\bIpsum"))).isEqualTo(List.of("Lorem Ipsum"));
		assertThat(StringUtils.sanitizeAndCompress(List.of("Lo%rem Ip\\\\\\\\sum", "Neq%ue Porr\\\\\\\\o"))).isEqualTo(List.of("Lorem Ipsum", "Neque Porro"));
	}
}
