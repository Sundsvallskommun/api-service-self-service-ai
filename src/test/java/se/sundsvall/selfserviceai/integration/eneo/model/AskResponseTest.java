package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AskResponseTest {

	private static final UUID SESSION_ID = UUID.randomUUID();
	private static final String QUESTION = "question";
	private static final String ANSWER = "answer";
	private static final FilePublic FILE_PUBLIC = FilePublic.builder().build();
	private static final List<FilePublic> FILES = List.of(FILE_PUBLIC);
	private static final Reference REFERENCE = Reference.builder().build();
	private static final List<Reference> REFERENCES = List.of(REFERENCE);
	private static final CompletionModel COMPLETION_MODEL = CompletionModel.builder().build();
	private static final Tools TOOLS = Tools.builder().build();

	@Test
	void builderPatternTest() {
		var bean = AskResponse.builder()
			.withSessionId(SESSION_ID)
			.withQuestion(QUESTION)
			.withAnswer(ANSWER)
			.withFiles(FILES)
			.withReferences(REFERENCES)
			.withCompletionModel(COMPLETION_MODEL)
			.withTools(TOOLS)
			.build();

		assertThat(bean.sessionId()).isEqualTo(SESSION_ID);
		assertThat(bean.question()).isEqualTo(QUESTION);
		assertThat(bean.answer()).isEqualTo(ANSWER);
		assertThat(bean.files()).isEqualTo(FILES);
		assertThat(bean.files()).containsExactly(FILE_PUBLIC);
		assertThat(bean.references()).isEqualTo(REFERENCES);
		assertThat(bean.references()).containsExactly(REFERENCE);
		assertThat(bean.completionModel()).isEqualTo(COMPLETION_MODEL);
		assertThat(bean.tools()).isEqualTo(TOOLS);
		assertThat(bean).hasOnlyFields("sessionId", "question", "answer", "files", "references", "completionModel", "tools");
	}

	@Test
	void constructorTest() {
		var bean = new AskResponse(SESSION_ID, QUESTION, ANSWER, FILES, REFERENCES, COMPLETION_MODEL, TOOLS);

		assertThat(bean.sessionId()).isEqualTo(SESSION_ID);
		assertThat(bean.question()).isEqualTo(QUESTION);
		assertThat(bean.answer()).isEqualTo(ANSWER);
		assertThat(bean.files()).isEqualTo(FILES);
		assertThat(bean.references()).isEqualTo(REFERENCES);
		assertThat(bean.completionModel()).isEqualTo(COMPLETION_MODEL);
		assertThat(bean.tools()).isEqualTo(TOOLS);
		assertThat(bean).hasOnlyFields("sessionId", "question", "answer", "files", "references", "completionModel", "tools");
	}

}
