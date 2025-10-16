package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ToolsTest {

	private static final Assistant ASSISTANT = Assistant.builder().build();
	private static final List<Assistant> ASSISTANTS = List.of(ASSISTANT);

	@Test
	void builderPatternTest() {
		var bean = Tools.builder()
			.withAssistants(ASSISTANTS)
			.build();

		assertThat(bean.assistants()).isEqualTo(ASSISTANTS).containsExactly(ASSISTANT);
		assertThat(bean).hasOnlyFields("assistants");
	}

	@Test
	void constructorTest() {
		var bean = new Tools(ASSISTANTS);

		assertThat(bean.assistants()).isEqualTo(ASSISTANTS);
		assertThat(bean).hasOnlyFields("assistants");
	}
}
