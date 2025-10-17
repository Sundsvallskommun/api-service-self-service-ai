package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SessionPublicTest {

	private static final UUID ID = UUID.randomUUID();
	private static final String NAME = "name";
	private static final Message MESSAGE = Message.builder().build();
	private static final List<Message> MESSAGES = List.of(MESSAGE);
	private static final SessionFeedback SESSION_FEEDBACK = SessionFeedback.builder().build();
	private static final OffsetDateTime CREATED_AT = OffsetDateTime.MIN;
	private static final OffsetDateTime UPDATED_AT = OffsetDateTime.MAX;

	@Test
	void builderPatternTest() {
		var bean = SessionPublic.builder()
			.withId(ID)
			.withName(NAME)
			.withCreatedAt(CREATED_AT)
			.withUpdatedAt(UPDATED_AT)
			.withMessages(MESSAGES)
			.withFeedback(SESSION_FEEDBACK)
			.build();

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.name()).isEqualTo(NAME);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean.messages()).isEqualTo(MESSAGES).containsExactly(MESSAGE);
		assertThat(bean.feedback()).isEqualTo(SESSION_FEEDBACK);
		assertThat(bean).hasOnlyFields("id", "name", "createdAt", "updatedAt", "messages", "feedback");
	}

	@Test
	void constructorTest() {
		var bean = new SessionPublic(ID, NAME, MESSAGES, SESSION_FEEDBACK, CREATED_AT, UPDATED_AT);

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.name()).isEqualTo(NAME);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean.messages()).isEqualTo(MESSAGES).containsExactly(MESSAGE);
		assertThat(bean.feedback()).isEqualTo(SESSION_FEEDBACK);
		assertThat(bean).hasOnlyFields("id", "name", "createdAt", "updatedAt", "messages", "feedback");
	}

	@Test
	void noDirtOnCreatedBean() {
		var bean = SessionPublic.builder().build();
		assertThat(bean).hasAllNullFieldsOrProperties();
	}

}
