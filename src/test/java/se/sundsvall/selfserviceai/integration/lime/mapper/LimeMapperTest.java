package se.sundsvall.selfserviceai.integration.lime.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.integration.intric.model.FilePublic;
import se.sundsvall.selfserviceai.integration.intric.model.Message;
import se.sundsvall.selfserviceai.integration.intric.model.SessionPublic;

class LimeMapperTest {

	@Test
	void fromFullSession() {

		// Arrange
		final var identity = "identity";
		final var isOrganization = true;
		final var customerNbr = 123456;
		final var sessionId = UUID.randomUUID();
		final var sessionCreated = OffsetDateTime.now().minusHours(12);
		final var sessionUpdated = OffsetDateTime.now().minusHours(11);
		final var sessionName = "sessionName";
		final var messageId = UUID.randomUUID();
		final var messageCreated = OffsetDateTime.now().minusHours(10);
		final var messageUpdated = OffsetDateTime.now().minusHours(9);
		final var fileId = UUID.randomUUID();
		final var fileCreated = OffsetDateTime.now().minusHours(8);
		final var fileUpdated = OffsetDateTime.now().minusHours(7);
		final var fileMimeType = "mimeType";
		final var fileName = "name";
		final var fileSize = 123;
		final var answer = "answer";
		final var question = "question";
		final var file = FilePublic.builder()
			.withCreatedAt(fileCreated)
			.withId(fileId)
			.withMimeType(fileMimeType)
			.withName(fileName)
			.withSize(fileSize)
			.withUpdatedAt(fileUpdated)
			.build();
		final var session = SessionPublic.builder()
			.withCreatedAt(sessionCreated)
			.withId(sessionId)
			.withMessages(List.of(Message.builder()
				.withAnswer(answer)
				.withCreatedAt(messageCreated)
				.withFiles(List.of(file))
				.withId(messageId)
				.withQuestion(question)
				.withUpdatedAt(messageUpdated)
				.build()))
			.withName(sessionName)
			.withUpdatedAt(sessionUpdated)
			.build();

		// Act
		final var bean = LimeMapper.toChatHistoryRequest(identity, isOrganization, customerNbr, session);

		// Assert
		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getIdentitetsnummer()).isEqualTo(identity);
		assertThat(bean.getIsOrganisation()).isEqualTo(isOrganization);
		assertThat(bean.getKundnummer()).isEqualTo(String.valueOf(customerNbr));
		assertThat(bean.getChatSession()).satisfies(s -> {
			assertThat(s.getCreatedAt()).isEqualTo(sessionCreated);
			assertThat(s.getFeedback()).isNull();
			assertThat(s.getId()).isEqualTo(sessionId.toString());
			assertThat(s.getName()).isEqualTo(sessionName);
			assertThat(s.getUpdatedAt()).isEqualTo(sessionUpdated);
			assertThat(s.getMessages()).satisfiesExactly(m -> {
				assertThat(m.getAnswer()).isEqualTo(answer);
				assertThat(m.getCompletionModel()).isNull();
				assertThat(m.getCreatedAt()).isEqualTo(messageCreated);
				assertThat(m.getId()).isEqualTo(messageId.toString());
				assertThat(m.getQuestion()).isEqualTo(question);
				assertThat(m.getReferences()).isNull();
				assertThat(m.getTools()).isNull();
				assertThat(m.getUpdatedAt()).isEqualTo(messageUpdated);
				assertThat(m.getFiles()).isNotNull();
				assertThat(((List<?>) m.getFiles())).hasSize(1).satisfiesExactly(entry -> {
					assertThat(entry).isSameAs(file);
				});
			});
		});
	}

	@Test
	void fromEmptySession() {

		// Act and assert
		assertThat(LimeMapper.toChatHistoryRequest(null, false, 0, SessionPublic.builder().build())).hasAllNullFieldsOrPropertiesExcept("kundnummer", "isOrganisation", "chatSession")
			.satisfies(r -> {
				assertThat(r.getKundnummer()).isEqualTo("0");
				assertThat(r.getIsOrganisation()).isFalse();
			})
			.extracting(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest::getChatSession).hasAllNullFieldsOrPropertiesExcept("messages")
			.extracting(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto::getMessages).satisfies(l -> assertThat(l).isEmpty());
	}

	@Test
	void fromNull() {

		// Act and assert
		assertThat(LimeMapper.toChatHistoryRequest(null, false, 0, null)).isNull();
	}
}
