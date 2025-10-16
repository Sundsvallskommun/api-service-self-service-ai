package se.sundsvall.selfserviceai.integration.eneo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CompletionModelTest {

	private static final UUID ID = UUID.randomUUID();
	private static final String NAME = "name";
	private static final String NICKNAME = "nickname";
	private static final String FAMILY = "family";
	private static final Integer TOKEN_LIMIT = 123;
	private static final Boolean IS_DEPRECATED = true;
	private static final Integer NR_BILLION_PARAMETERS = 123;
	private static final String HF_LINK = "hfLink";
	private static final String STABILITY = "stability";
	private static final String HOSTING = "hosting";
	private static final Boolean OPEN_SOURCE = true;
	private static final String DESCRIPTION = "description";
	private static final String DEPLOYMENT_NAME = "deploymentName";
	private static final String ORG = "org";
	private static final boolean VISION = false;
	private static final boolean REASONING = false;
	private static final String BASE_URL = "baseUrl";
	private static final boolean IS_ORG_ENABLED = false;
	private static final boolean IS_ORG_DEFAULT = false;
	private static final OffsetDateTime CREATED_AT = OffsetDateTime.MAX;
	private static final OffsetDateTime UPDATED_AT = OffsetDateTime.MIN;

	@Test
	void builderPatternTest() {
		var bean = CompletionModel.builder()
			.withId(ID)
			.withName(NAME)
			.withNickname(NICKNAME)
			.withFamily(FAMILY)
			.withTokenLimit(TOKEN_LIMIT)
			.withIsDeprecated(IS_DEPRECATED)
			.withNrBillionParameters(NR_BILLION_PARAMETERS)
			.withHfLink(HF_LINK)
			.withStability(STABILITY)
			.withHosting(HOSTING)
			.withOpenSource(OPEN_SOURCE)
			.withDescription(DESCRIPTION)
			.withOrg(ORG)
			.withDeploymentName(DEPLOYMENT_NAME)
			.withIsOrgEnabled(IS_ORG_DEFAULT)
			.withIsOrgEnabled(IS_ORG_ENABLED)
			.withCreatedAt(CREATED_AT)
			.withUpdatedAt(UPDATED_AT)
			.withVision(VISION)
			.withReasoning(REASONING)
			.withBaseUrl(BASE_URL)
			.build();

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.name()).isEqualTo(NAME);
		assertThat(bean.nickname()).isEqualTo(NICKNAME);
		assertThat(bean.family()).isEqualTo(FAMILY);
		assertThat(bean.tokenLimit()).isEqualTo(TOKEN_LIMIT);
		assertThat(bean.isDeprecated()).isEqualTo(IS_DEPRECATED);
		assertThat(bean.nrBillionParameters()).isEqualTo(NR_BILLION_PARAMETERS);
		assertThat(bean.hfLink()).isEqualTo(HF_LINK);
		assertThat(bean.stability()).isEqualTo(STABILITY);
		assertThat(bean.hosting()).isEqualTo(HOSTING);
		assertThat(bean.openSource()).isEqualTo(OPEN_SOURCE);
		assertThat(bean.description()).isEqualTo(DESCRIPTION);
		assertThat(bean.org()).isEqualTo(ORG);
		assertThat(bean.deploymentName()).isEqualTo(DEPLOYMENT_NAME);
		assertThat(bean.isOrgEnabled()).isEqualTo(IS_ORG_DEFAULT);
		assertThat(bean.isOrgEnabled()).isEqualTo(IS_ORG_ENABLED);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean.vision()).isEqualTo(VISION);
		assertThat(bean.reasoning()).isEqualTo(REASONING);
		assertThat(bean.baseUrl()).isEqualTo(BASE_URL);
		assertThat(bean).hasOnlyFields("id", "name", "nickname", "family", "tokenLimit", "isDeprecated", "nrBillionParameters", "hfLink", "stability", "hosting",
			"openSource", "description", "deploymentName", "org", "vision", "reasoning", "baseUrl", "isOrgEnabled", "isOrgDefault", "createdAt", "updatedAt");
	}

	@Test
	void constructorTest() {
		var bean = new CompletionModel(ID, NAME, NICKNAME, FAMILY, TOKEN_LIMIT, IS_DEPRECATED, NR_BILLION_PARAMETERS, HF_LINK, STABILITY, HOSTING, OPEN_SOURCE, DESCRIPTION, DEPLOYMENT_NAME, ORG, VISION, REASONING, BASE_URL, IS_ORG_DEFAULT, IS_ORG_ENABLED,
			CREATED_AT,
			UPDATED_AT);

		assertThat(bean.id()).isEqualTo(ID);
		assertThat(bean.name()).isEqualTo(NAME);
		assertThat(bean.nickname()).isEqualTo(NICKNAME);
		assertThat(bean.family()).isEqualTo(FAMILY);
		assertThat(bean.tokenLimit()).isEqualTo(TOKEN_LIMIT);
		assertThat(bean.isDeprecated()).isEqualTo(IS_DEPRECATED);
		assertThat(bean.nrBillionParameters()).isEqualTo(NR_BILLION_PARAMETERS);
		assertThat(bean.hfLink()).isEqualTo(HF_LINK);
		assertThat(bean.stability()).isEqualTo(STABILITY);
		assertThat(bean.hosting()).isEqualTo(HOSTING);
		assertThat(bean.openSource()).isEqualTo(OPEN_SOURCE);
		assertThat(bean.description()).isEqualTo(DESCRIPTION);
		assertThat(bean.org()).isEqualTo(ORG);
		assertThat(bean.deploymentName()).isEqualTo(DEPLOYMENT_NAME);
		assertThat(bean.isOrgEnabled()).isEqualTo(IS_ORG_DEFAULT);
		assertThat(bean.isOrgEnabled()).isEqualTo(IS_ORG_ENABLED);
		assertThat(bean.createdAt()).isEqualTo(CREATED_AT);
		assertThat(bean.updatedAt()).isEqualTo(UPDATED_AT);
		assertThat(bean.vision()).isEqualTo(VISION);
		assertThat(bean.reasoning()).isEqualTo(REASONING);
		assertThat(bean.baseUrl()).isEqualTo(BASE_URL);
		assertThat(bean).hasOnlyFields("id", "name", "nickname", "family", "tokenLimit", "isDeprecated", "nrBillionParameters", "hfLink", "stability", "hosting",
			"openSource", "description", "deploymentName", "org", "vision", "reasoning", "baseUrl", "isOrgEnabled", "isOrgDefault", "createdAt", "updatedAt");

	}

}
