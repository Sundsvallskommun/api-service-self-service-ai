package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FilePublicTest {

	private static final UUID ID = UUID.randomUUID();
	private static final String NAME = "name";
	private static final String MIME_TYPE = "mimeType";
	private static final Integer SIZE = 123;
	private static final OffsetDateTime CREATED_AT = OffsetDateTime.MIN;
	private static final OffsetDateTime UPDATED_AT = OffsetDateTime.MAX;
	private static final String TRANSCRIPTION = "transcription";

	@Test
	void builderPatternTest() {
		var bean = FilePublic.builder()
			.withId(ID)
			.withName(NAME)
			.withMimeType(MIME_TYPE)
			.withSize(SIZE)
			.withCreatedAt(CREATED_AT)
			.withUpdatedAt(UPDATED_AT)
			.withTranscription(TRANSCRIPTION)
			.build();

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.name()).isEqualTo(NAME);
		assertThat(bean.mimeType()).isEqualTo(MIME_TYPE);
		assertThat(bean.size()).isEqualTo(SIZE);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean.transcription()).isEqualTo(TRANSCRIPTION);
		assertThat(bean).hasOnlyFields("id", "name", "mimeType", "size", "createdAt", "updatedAt", "transcription");
	}

	@Test
	void constructorTest() {
		var bean = new FilePublic(ID, NAME, MIME_TYPE, SIZE, CREATED_AT, UPDATED_AT, TRANSCRIPTION);

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.name()).isEqualTo(NAME);
		assertThat(bean.mimeType()).isEqualTo(MIME_TYPE);
		assertThat(bean.size()).isEqualTo(SIZE);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean.transcription()).isEqualTo(TRANSCRIPTION);
		assertThat(bean).hasOnlyFields("id", "name", "mimeType", "size", "createdAt", "updatedAt", "transcription");
	}

	@Test
	void noDirtOnCreatedBean() {
		var bean = FilePublic.builder().build();
		assertThat(bean).hasAllNullFieldsOrProperties();
	}

}
