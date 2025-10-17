package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class AskAssistantTest {

	@Test
	void builderPatternTest() {
		var question = "question";
		var file1 = FilePublic.builder().build();
		var file2 = FilePublic.builder().build();
		var files = List.of(file1, file2);

		var bean = AskAssistant.builder()
			.withQuestion(question)
			.withFiles(files)
			.build();

		assertThat(bean.question()).isEqualTo(question);
		assertThat(bean.files()).hasSize(2).containsExactlyInAnyOrder(file1, file2);
		assertThat(bean).hasOnlyFields("question", "files");
	}

	@Test
	void constructorTest() {
		var question = "question";
		var file1 = FilePublic.builder().build();
		var file2 = FilePublic.builder().build();
		var files = List.of(file1, file2);

		var bean = new AskAssistant(question, files);

		assertThat(bean.question()).isEqualTo(question);
		assertThat(bean.files()).hasSize(2).containsExactlyInAnyOrder(file1, file2);
		assertThat(bean).hasOnlyFields("question", "files");
	}

	@Test
	void noDirtOnCreatedBean() {
		var bean = AskAssistant.builder().build();
		assertThat(bean).hasAllNullFieldsOrProperties();
	}

}
