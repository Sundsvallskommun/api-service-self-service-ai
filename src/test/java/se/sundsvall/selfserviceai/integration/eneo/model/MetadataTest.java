package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MetadataTest {

	private static final UUID EMBEDDING_MODEL_ID = UUID.randomUUID();
	private static final String URL = "url";
	private static final String TITLE = "title";
	private static final int SIZE = 1;

	@Test
	void builderPatternTest() {
		var bean = Metadata.builder()
			.withEmbeddingModelId(EMBEDDING_MODEL_ID)
			.withUrl(URL)
			.withTitle(TITLE)
			.withSize(SIZE)
			.build();

		assertThat(bean.embeddingModelId()).isEqualTo(EMBEDDING_MODEL_ID);
		assertThat(bean.url()).isEqualTo(URL);
		assertThat(bean.title()).isEqualTo(TITLE);
		assertThat(bean.size()).isEqualTo(SIZE);
		assertThat(bean).hasOnlyFields("embeddingModelId", "url", "title", "size");
	}

	@Test
	void constructorTest() {
		var bean = new Metadata(EMBEDDING_MODEL_ID, URL, TITLE, SIZE);

		assertThat(bean.embeddingModelId()).isEqualTo(EMBEDDING_MODEL_ID);
		assertThat(bean.url()).isEqualTo(URL);
		assertThat(bean.title()).isEqualTo(TITLE);
		assertThat(bean.size()).isEqualTo(SIZE);
		assertThat(bean).hasOnlyFields("embeddingModelId", "url", "title", "size");
	}

	@Test
	void noDirtOnCreatedBean() {
		var bean = Metadata.builder().build();
		assertThat(bean).hasAllNullFieldsOrPropertiesExcept("size");
	}

}
