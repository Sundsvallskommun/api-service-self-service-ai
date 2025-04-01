package se.sundsvall.selfserviceai.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.OffsetDateTime;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ModelTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(Model.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var baseUrl = "baseUrl";
		final var createdAt = OffsetDateTime.now().minusDays(7);
		final var deploymentName = "deploymentName";
		final var deprecated = true;
		final var description = "description";
		final var family = "family";
		final var hfLink = "hfLink";
		final var hosting = "hosting";
		final var id = "id";
		final var name = "name";
		final var nickname = "nickname";
		final var nrBillionParameters = 123;
		final var openSource = true;
		final var org = "org";
		final var orgDefault = true;
		final var orgEnabled = true;
		final var reasoning = true;
		final var stability = "stability";
		final var tokenLimit = 456;
		final var updatedAt = OffsetDateTime.now();
		final var vision = true;

		final var bean = Model.builder()
			.withBaseUrl(baseUrl)
			.withCreatedAt(createdAt)
			.withDeploymentName(deploymentName)
			.withDeprecated(deprecated)
			.withDescription(description)
			.withFamily(family)
			.withHfLink(hfLink)
			.withHosting(hosting)
			.withId(id)
			.withName(name)
			.withNickname(nickname)
			.withNrBillionParameters(nrBillionParameters)
			.withOpenSource(openSource)
			.withOrg(org)
			.withOrgDefault(orgDefault)
			.withOrgEnabled(orgEnabled)
			.withReasoning(reasoning)
			.withStability(stability)
			.withTokenLimit(tokenLimit)
			.withUpdatedAt(updatedAt)
			.withVision(vision)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getBaseUrl()).isEqualTo(baseUrl);
		assertThat(bean.getCreatedAt()).isEqualTo(createdAt);
		assertThat(bean.getDeploymentName()).isEqualTo(deploymentName);
		assertThat(bean.getDeprecated()).isEqualTo(deprecated);
		assertThat(bean.getDescription()).isEqualTo(description);
		assertThat(bean.getFamily()).isEqualTo(family);
		assertThat(bean.getHfLink()).isEqualTo(hfLink);
		assertThat(bean.getHosting()).isEqualTo(hosting);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getName()).isEqualTo(name);
		assertThat(bean.getNickname()).isEqualTo(nickname);
		assertThat(bean.getNrBillionParameters()).isEqualTo(nrBillionParameters);
		assertThat(bean.getOpenSource()).isEqualTo(openSource);
		assertThat(bean.getOrg()).isEqualTo(org);
		assertThat(bean.isOrgDefault()).isEqualTo(orgDefault);
		assertThat(bean.isOrgEnabled()).isEqualTo(orgEnabled);
		assertThat(bean.isReasoning()).isEqualTo(reasoning);
		assertThat(bean.getStability()).isEqualTo(stability);
		assertThat(bean.getTokenLimit()).isEqualTo(tokenLimit);
		assertThat(bean.getUpdatedAt()).isEqualTo(updatedAt);
		assertThat(bean.isVision()).isEqualTo(vision);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Model.builder().build()).hasAllNullFieldsOrPropertiesExcept("nrBillionParameters", "vision", "reasoning", "orgEnabled", "orgDefault")
			.hasFieldOrPropertyWithValue("nrBillionParameters", 0)
			.hasFieldOrPropertyWithValue("vision", false)
			.hasFieldOrPropertyWithValue("reasoning", false)
			.hasFieldOrPropertyWithValue("orgEnabled", false)
			.hasFieldOrPropertyWithValue("orgDefault", false);

		assertThat(new Model()).hasAllNullFieldsOrPropertiesExcept("nrBillionParameters", "vision", "reasoning", "orgEnabled", "orgDefault")
			.hasFieldOrPropertyWithValue("nrBillionParameters", 0)
			.hasFieldOrPropertyWithValue("vision", false)
			.hasFieldOrPropertyWithValue("reasoning", false)
			.hasFieldOrPropertyWithValue("orgEnabled", false)
			.hasFieldOrPropertyWithValue("orgDefault", false);
	}
}
