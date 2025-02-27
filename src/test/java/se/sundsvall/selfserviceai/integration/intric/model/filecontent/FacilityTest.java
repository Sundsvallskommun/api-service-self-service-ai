package se.sundsvall.selfserviceai.integration.intric.model.filecontent;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FacilityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(Facility.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));

		MatcherAssert.assertThat(Facility.Metadata.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));

	}

	@Test
	void testBuilderMethodsForFacility() {
		final var address = Address.builder().build();
		final var agreements = List.of(Agreement.builder().build());
		final var commitmentEndDate = LocalDate.now().plusDays(1);
		final var commitmentStartDate = LocalDate.now().minusDays(1);
		final var facilityId = "facilityId";
		final var information = List.of(Facility.Metadata.builder().build());
		final var invoices = List.of(Invoice.builder().build());
		final var lastModifiedDate = LocalDate.now();
		final var measurements = List.of(MeasurementData.builder().build());
		final var placementId = 123;
		final var type = "type";

		final var bean = Facility.builder()
			.withAddress(address)
			.withAgreements(agreements)
			.withCommitmentEndDate(commitmentEndDate)
			.withCommitmentStartDate(commitmentStartDate)
			.withFacilityId(facilityId)
			.withInformation(information)
			.withInvoices(invoices)
			.withLastModifiedDate(lastModifiedDate)
			.withMeasurements(measurements)
			.withPlacementId(placementId)
			.withType(type)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAddress()).isEqualTo(address);
		assertThat(bean.getAgreements()).isEqualTo(agreements);
		assertThat(bean.getCommitmentEndDate()).isEqualTo(commitmentEndDate);
		assertThat(bean.getCommitmentStartDate()).isEqualTo(commitmentStartDate);
		assertThat(bean.getFacilityId()).isEqualTo(facilityId);
		assertThat(bean.getInformation()).isEqualTo(information);
		assertThat(bean.getInvoices()).isEqualTo(invoices);
		assertThat(bean.getLastModifiedDate()).isEqualTo(lastModifiedDate);
		assertThat(bean.getMeasurements()).isEqualTo(measurements);
		assertThat(bean.getPlacementId()).isEqualTo(placementId);
		assertThat(bean.getType()).isEqualTo(type);
	}

	void testBuilderMethodsForMetadata() {
		final var displayName = "displayName";
		final var name = "name";
		final var type = "type";
		final var value = "value";

		final var bean = Facility.Metadata.builder()
			.withDisplayName(displayName)
			.withName(name)
			.withType(type)
			.withValue(value)
			.build();

		assertThat(bean.getDisplayName()).isEqualTo(displayName);
		assertThat(bean.getName()).isEqualTo(name);
		assertThat(bean.getType()).isEqualTo(type);
		assertThat(bean.getValue()).isEqualTo(value);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Facility.builder().build()).hasAllNullFieldsOrPropertiesExcept("agreements", "measurements", "invoices")
			.hasFieldOrPropertyWithValue("agreements", emptyList())
			.hasFieldOrPropertyWithValue("measurements", emptyList())
			.hasFieldOrPropertyWithValue("invoices", emptyList());
		assertThat(new Facility()).hasAllNullFieldsOrPropertiesExcept("agreements", "measurements", "invoices")
			.hasFieldOrPropertyWithValue("agreements", emptyList())
			.hasFieldOrPropertyWithValue("measurements", emptyList())
			.hasFieldOrPropertyWithValue("invoices", emptyList());

		assertThat(Facility.Metadata.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Facility.Metadata()).hasAllNullFieldsOrProperties();
	}
}
