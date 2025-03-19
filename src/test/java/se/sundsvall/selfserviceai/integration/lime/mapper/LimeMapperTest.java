package se.sundsvall.selfserviceai.integration.lime.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static se.sundsvall.selfserviceai.TestFactory.ANSWER;
import static se.sundsvall.selfserviceai.TestFactory.COMPLETION_MODEL_CREATED;
import static se.sundsvall.selfserviceai.TestFactory.COMPLETION_MODEL_ID;
import static se.sundsvall.selfserviceai.TestFactory.COMPLETION_MODEL_UPDATED;
import static se.sundsvall.selfserviceai.TestFactory.DEPLOYMENT_NAME;
import static se.sundsvall.selfserviceai.TestFactory.DEPRECATED;
import static se.sundsvall.selfserviceai.TestFactory.DESCRIPTION;
import static se.sundsvall.selfserviceai.TestFactory.FAMILY;
import static se.sundsvall.selfserviceai.TestFactory.HF_LINK;
import static se.sundsvall.selfserviceai.TestFactory.HOSTING;
import static se.sundsvall.selfserviceai.TestFactory.MESSAGE_CREATED;
import static se.sundsvall.selfserviceai.TestFactory.MESSAGE_ID;
import static se.sundsvall.selfserviceai.TestFactory.MESSAGE_UPDATED;
import static se.sundsvall.selfserviceai.TestFactory.NAME;
import static se.sundsvall.selfserviceai.TestFactory.NICKNAME;
import static se.sundsvall.selfserviceai.TestFactory.NR_BILLION_PARAMETERS;
import static se.sundsvall.selfserviceai.TestFactory.OPEN_SOURCE;
import static se.sundsvall.selfserviceai.TestFactory.ORG;
import static se.sundsvall.selfserviceai.TestFactory.ORG_DEFAULT;
import static se.sundsvall.selfserviceai.TestFactory.ORG_ENABLED;
import static se.sundsvall.selfserviceai.TestFactory.PARTY_ID;
import static se.sundsvall.selfserviceai.TestFactory.QUESTION;
import static se.sundsvall.selfserviceai.TestFactory.SESSION_CREATED;
import static se.sundsvall.selfserviceai.TestFactory.SESSION_ID;
import static se.sundsvall.selfserviceai.TestFactory.SESSION_NAME;
import static se.sundsvall.selfserviceai.TestFactory.SESSION_UPDATED;
import static se.sundsvall.selfserviceai.TestFactory.STABILITY;
import static se.sundsvall.selfserviceai.TestFactory.TOKEN_LIMIT;
import static se.sundsvall.selfserviceai.TestFactory.VISION;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikCompletionModelDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikToolsDto;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.integration.intric.model.Assistant;
import se.sundsvall.selfserviceai.integration.intric.model.FilePublic;
import se.sundsvall.selfserviceai.integration.intric.model.Reference;
import se.sundsvall.selfserviceai.integration.intric.model.SessionPublic;

class LimeMapperTest {

	@Test
	void fromFullSession() {

		// Arrange
		final var customerNbr = "123456";
		final var session = TestFactory.createSession();

		// Act
		final var bean = LimeMapper.toChatHistoryRequest(PARTY_ID, customerNbr, session);

		// Assert
		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getPartyId()).isEqualTo(PARTY_ID);
		assertThat(bean.getKundnummer()).isEqualTo(String.valueOf(customerNbr));
		assertThat(bean.getChatSession()).satisfies(this::assertChatSession);

	}

	private void assertChatSession(@NotNull ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto s) {
		assertThat(s.getCreatedAt()).isEqualTo(SESSION_CREATED);
		assertThat(s.getFeedback()).isNotNull().satisfies(this::assertFeedback);
		assertThat(s.getId()).isEqualTo(SESSION_ID.toString());
		assertThat(s.getName()).isEqualTo(SESSION_NAME);
		assertThat(s.getUpdatedAt()).isEqualTo(SESSION_UPDATED);
		assertThat(s.getMessages()).hasSize(1).satisfiesExactly(this::assertMessages);
	}

	private void assertFeedback(Object f) {
		assertThat(f).usingRecursiveComparison().isEqualTo(TestFactory.createSessionFeedback());
	}

	private void assertMessages(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikMessageDto m) {
		assertThat(m.getAnswer()).isEqualTo(ANSWER);
		assertThat(m.getCompletionModel()).isNotNull().satisfies(this::assertCompletionModel);
		assertThat(m.getCreatedAt()).isEqualTo(MESSAGE_CREATED);
		assertThat(m.getId()).isEqualTo(MESSAGE_ID.toString());
		assertThat(m.getQuestion()).isEqualTo(QUESTION);
		assertThat(m.getReferences()).isNotNull().asInstanceOf(LIST).hasSize(1).satisfiesExactly(this::assertReference);
		assertThat(m.getTools()).isNotNull().satisfies(this::assertTools);
		assertThat(m.getUpdatedAt()).isEqualTo(MESSAGE_UPDATED);
		assertThat(m.getFiles()).isNotNull().asInstanceOf(LIST).hasSize(1).satisfiesExactly(this::assertFile);
	}

	private void assertFile(Object f) {
		assertThat(f)
			.isInstanceOf(FilePublic.class)
			.usingRecursiveAssertion()
			.isEqualTo(TestFactory.createFilePublic());
	}

	private void assertCompletionModel(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikCompletionModelDto c) {
		assertThat(c.getCreatedAt()).isEqualTo(COMPLETION_MODEL_CREATED);
		assertThat(c.getDeploymentName()).isEqualTo(DEPLOYMENT_NAME);
		assertThat(c.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(c.getFamily()).isEqualTo(FAMILY);
		assertThat(c.getHfLink()).isEqualTo(HF_LINK);
		assertThat(c.getHosting()).isEqualTo(HOSTING);
		assertThat(c.getId()).isEqualTo(COMPLETION_MODEL_ID.toString());
		assertThat(c.getIsDeprecated()).isEqualTo(DEPRECATED);
		assertThat(c.getIsOrgDefault()).isEqualTo(ORG_DEFAULT);
		assertThat(c.getIsOrgEnabled()).isEqualTo(ORG_ENABLED);
		assertThat(c.getName()).isEqualTo(NAME);
		assertThat(c.getNickname()).isEqualTo(NICKNAME);
		assertThat(c.getNrBillionParameters()).isEqualTo(NR_BILLION_PARAMETERS);
		assertThat(c.getOpenSource()).isEqualTo(OPEN_SOURCE);
		assertThat(c.getOrg()).isEqualTo(ORG);
		assertThat(c.getStability()).isEqualTo(STABILITY);
		assertThat(c.getTokenLimit()).isEqualTo(TOKEN_LIMIT);
		assertThat(c.getUpdatedAt()).isEqualTo(COMPLETION_MODEL_UPDATED);
		assertThat(c.getVision()).isEqualTo(VISION);
	}

	private void assertTools(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikToolsDto t) {
		assertThat(t.getAssistants())
			.isNotNull()
			.asInstanceOf(LIST).hasSize(1).satisfiesExactly(this::assertAssistant);
	}

	private void assertAssistant(Object a) {
		assertThat(a)
			.isInstanceOf(Assistant.class)
			.usingRecursiveAssertion()
			.isEqualTo(TestFactory.createAssistant());
	}

	private void assertReference(Object r) {
		assertThat(r)
			.isInstanceOf(Reference.class)
			.usingRecursiveAssertion()
			.isEqualTo(TestFactory.createReference());
	}

	@Test
	void fromEmptySession() {

		// Act and assert
		assertThat(LimeMapper.toChatHistoryRequest(null, null, SessionPublic.builder().build())).hasAllNullFieldsOrPropertiesExcept("chatSession")
			.extracting(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest::getChatSession).hasAllNullFieldsOrPropertiesExcept("messages")
			.extracting(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikChatSessionDto::getMessages).satisfies(l -> assertThat(l).isEmpty());
	}

	@Test
	void fromNull() {

		// Act and assert
		assertThat(LimeMapper.toChatHistoryRequest(null, null, null)).isNull();
	}
}
