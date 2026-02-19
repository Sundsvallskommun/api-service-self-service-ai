package se.sundsvall.selfserviceai.integration.eneo.model.filecontent;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

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
	}

	@Test
	void testBuilderMethodsForFacility() {
		final var address = Address.builder().build();
		final var agreements = List.of(Agreement.builder().build());
		final var installedBases = List.of(InstalledBase.builder().build());
		final var facilityId = "facilityId";
		final var invoices = List.of(Invoice.builder().build());
		final var measurements = List.of(MeasurementData.builder().build());

		final var bean = Facility.builder()
			.withAddress(address)
			.withAgreements(agreements)
			.withInstalledBases(installedBases)
			.withFacilityId(facilityId)
			.withInvoices(invoices)
			.withMeasurements(measurements)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAddress()).isEqualTo(address);
		assertThat(bean.getAgreements()).isEqualTo(agreements);
		assertThat(bean.getInstalledBases()).isEqualTo(installedBases);
		assertThat(bean.getFacilityId()).isEqualTo(facilityId);
		assertThat(bean.getInvoices()).isEqualTo(invoices);
		assertThat(bean.getMeasurements()).isEqualTo(measurements);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Facility.builder().build()).hasAllNullFieldsOrPropertiesExcept("agreements", "installedBases", "invoices", "measurements")
			.hasFieldOrPropertyWithValue("agreements", emptyList())
			.hasFieldOrPropertyWithValue("installedBases", emptyList())
			.hasFieldOrPropertyWithValue("invoices", emptyList())
			.hasFieldOrPropertyWithValue("measurements", emptyList());
		assertThat(new Facility()).hasAllNullFieldsOrPropertiesExcept("agreements", "installedBases", "invoices", "measurements")
			.hasFieldOrPropertyWithValue("agreements", emptyList())
			.hasFieldOrPropertyWithValue("installedBases", emptyList())
			.hasFieldOrPropertyWithValue("invoices", emptyList())
			.hasFieldOrPropertyWithValue("measurements", emptyList());
	}
}
