package se.sundsvall.selfserviceai.integration.intric.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class InformationFileTest {

	@Test
	void implementsMultipartFile() {
		assertThat(MultipartFile.class).isAssignableFrom(InformationFile.class);
	}

	@Test
	void emptyObject() {
		assertThat(List.of(InformationFile.create(), new InformationFile())).isNotEmpty().allSatisfy(entry -> {
			assertThat(entry.getBytes()).isNull();
			assertThat(entry.getContentType()).isEqualTo("application/json");
			assertThat(entry.getInputStream()).isNull();
			assertThat(entry.getName()).isEqualTo("selfservice-ai-data.json");
			assertThat(entry.getOriginalFilename()).isEqualTo("selfservice-ai-data.json");
			assertThat(entry.getResource()).isNotNull();
			assertThat(entry.getSize()).isZero();
			assertThat(entry.isEmpty()).isTrue();
		});
	}

	@Test
	void builderMethods() throws Exception {
		final var data = "some data";
		final var bean = InformationFile.create()
			.withData(data);

		assertThat(bean.getBytes()).isEqualTo(data.getBytes(Charset.defaultCharset()));
		assertThat(bean.getContentType()).isEqualTo("application/json");
		assertThat(bean.getInputStream()).isNotNull();
		assertThat(bean.getName()).isEqualTo("selfservice-ai-data.json");
		assertThat(bean.getOriginalFilename()).isEqualTo("selfservice-ai-data.json");
		assertThat(bean.getResource()).isNotNull();
		assertThat(bean.getSize()).isEqualTo(data.getBytes(Charset.defaultCharset()).length);
		assertThat(bean.isEmpty()).isFalse();
	}

	@Test
	void transferTo() throws Exception {
		final var data = "some data";
		final var bean = InformationFile.create()
			.withData(data);
		final var file = File.createTempFile("unittest", null);
		file.deleteOnExit();

		bean.transferTo(file);

		assertThat(Files.readString(Path.of(file.getPath()), Charset.defaultCharset())).isEqualTo(data);
	}

	@Test
	void transferToWhenNull() throws Exception {
		final var fileMock = Mockito.mock(File.class);
		final var bean = InformationFile.create();

		assertDoesNotThrow(() -> bean.transferTo(fileMock));

		verifyNoInteractions(fileMock);
	}

	@Test
	void isEmpty() {
		assertThat(InformationFile.create().isEmpty()).isTrue();
		assertThat(InformationFile.create().withData("").isEmpty()).isTrue();
		assertThat(InformationFile.create().withData("1").isEmpty()).isFalse();
	}

	@Test
	void withData() throws IOException {
		assertThat(InformationFile.create().withData(null).getBytes()).isNull();
		assertThat(InformationFile.create().withData("").getBytes()).isEmpty();
		assertThat(InformationFile.create().withData("1").getBytes()).isNotEmpty();
	}

}
