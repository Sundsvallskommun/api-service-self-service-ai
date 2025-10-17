package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReferenceTest {

	private static final UUID ID = UUID.randomUUID();
	private static final Metadata METADATA = Metadata.builder().build();
	private static final UUID GROUP_ID = UUID.randomUUID();
	private static final UUID WEBSITE_ID = UUID.randomUUID();
	private static final OffsetDateTime CREATED_AT = OffsetDateTime.MIN;
	private static final OffsetDateTime UPDATED_AT = OffsetDateTime.MAX;
	private static final int SCORE = 1;

	@Test
	void builderPatternTest() {
		var bean = Reference.builder()
			.withId(ID)
			.withMetadata(METADATA)
			.withGroupId(GROUP_ID)
			.withWebsiteId(WEBSITE_ID)
			.withCreatedAt(CREATED_AT)
			.withUpdatedAt(UPDATED_AT)
			.withScore(SCORE)
			.build();

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.metadata()).isEqualTo(METADATA);
		assertThat(bean.groupId()).isEqualTo(GROUP_ID);
		assertThat(bean.websiteId()).isEqualTo(WEBSITE_ID);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean.score()).isEqualTo(SCORE);
		assertThat(bean).hasOnlyFields("id", "metadata", "groupId", "websiteId", "createdAt", "updatedAt", "score");
	}

	@Test
	void constructorTest() {
		var bean = new Reference(ID, METADATA, GROUP_ID, WEBSITE_ID, CREATED_AT, UPDATED_AT, SCORE);

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.metadata()).isEqualTo(METADATA);
		assertThat(bean.groupId()).isEqualTo(GROUP_ID);
		assertThat(bean.websiteId()).isEqualTo(WEBSITE_ID);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean.score()).isEqualTo(SCORE);
		assertThat(bean).hasOnlyFields("id", "metadata", "groupId", "websiteId", "createdAt", "updatedAt", "score");
	}

	@Test
	void noDirtOnCreatedBean() {
		var bean = Reference.builder().build();
		assertThat(bean).hasAllNullFieldsOrPropertiesExcept("score");
		assertThat(bean.score()).isEqualTo(0);
	}

}
