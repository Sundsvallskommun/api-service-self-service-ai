package se.sundsvall.selfserviceai.integration.eneo.model;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssistantTest {

	private static final UUID ID = UUID.randomUUID();
	private static final String HANDLE = "handle";

	@Test
	void builderPatternTest() {
		var bean = Assistant.builder()
			.withId(ID)
			.withHandle(HANDLE)
			.build();

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.handle()).isEqualTo(HANDLE);
		assertThat(bean).hasOnlyFields("id", "handle");
	}

	@Test
	void constructorTest() {
		var bean = new Assistant(ID, HANDLE);

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.handle()).isEqualTo(HANDLE);
		assertThat(bean).hasOnlyFields("id", "handle");
	}

	@Test
	void noDirtOnCreatedBean() {
		var bean = Assistant.builder().build();
		assertThat(bean).hasAllNullFieldsOrProperties();
	}

}
