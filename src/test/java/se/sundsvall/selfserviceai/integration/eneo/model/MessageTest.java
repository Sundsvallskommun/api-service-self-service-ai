package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MessageTest {

	private static final UUID ID = UUID.randomUUID();
	private static final String QUESTION = "question";
	private static final String ANSWER = "answer";
	private static final CompletionModel COMPLETION_MODEL = CompletionModel.builder().build();
	private static final Reference REFERENCE = Reference.builder().build();
	private static final List<Reference> REFERENCES = List.of(REFERENCE);
	private static final FilePublic FILE_PUBLIC = FilePublic.builder().build();
	private static final List<FilePublic> FILES = List.of(FILE_PUBLIC);
	private static final Tools TOOLS = Tools.builder().build();
	private static final OffsetDateTime CREATED_AT = OffsetDateTime.MIN;
	private static final OffsetDateTime UPDATED_AT = OffsetDateTime.MAX;

	@Test
	void builderPatternTest() {
		var bean = Message.builder()
			.withId(ID)
			.withQuestion(QUESTION)
			.withAnswer(ANSWER)
			.withCompletionModel(COMPLETION_MODEL)
			.withReferences(REFERENCES)
			.withFiles(FILES)
			.withTools(TOOLS)
			.withCreatedAt(CREATED_AT)
			.withUpdatedAt(UPDATED_AT)
			.build();

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.question()).isEqualTo(QUESTION);
		assertThat(bean.answer()).isEqualTo(ANSWER);
		assertThat(bean.completionModel()).isEqualTo(COMPLETION_MODEL);
		assertThat(bean.references()).isEqualTo(REFERENCES).containsExactly(REFERENCE);
		assertThat(bean.files()).isEqualTo(FILES).containsExactly(FILE_PUBLIC);
		assertThat(bean.tools()).isEqualTo(TOOLS);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean).hasOnlyFields("id", "question", "answer", "completionModel", "references", "files", "tools", "createdAt", "updatedAt");
	}

	@Test
	void constructorTest() {
		var bean = new Message(ID, QUESTION, ANSWER, COMPLETION_MODEL, REFERENCES, FILES, TOOLS, CREATED_AT, UPDATED_AT);

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.question()).isEqualTo(QUESTION);
		assertThat(bean.answer()).isEqualTo(ANSWER);
		assertThat(bean.completionModel()).isEqualTo(COMPLETION_MODEL);
		assertThat(bean.references()).isEqualTo(REFERENCES).containsExactly(REFERENCE);
		assertThat(bean.files()).isEqualTo(FILES).containsExactly(FILE_PUBLIC);
		assertThat(bean.tools()).isEqualTo(TOOLS);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean).hasOnlyFields("id", "question", "answer", "completionModel", "references", "files", "tools", "createdAt", "updatedAt");
	}

	@Test
	void noDirtOnCreatedBean() {
		var bean = Message.builder().build();
		assertThat(bean).hasAllNullFieldsOrProperties();
	}

}
