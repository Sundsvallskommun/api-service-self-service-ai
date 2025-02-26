package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;

class IntricMapperTest {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

	@Test
	void toInstalledBase() {
		// Arrange
		final var input = createCustomer();

		// Act
		final var result = IntricMapper.toInstalledBase(input);

		// Assert
		assertThat(result.getCustomerNumber()).isEqualTo(input.getCustomerNumber());
		assertThat(result.getPartyId()).isEqualTo(input.getPartyId());
		assertThat(result.getFacilities()).hasSize(2)
			.allSatisfy(f -> {
				assertThat(f.getMeasurements()).isEmpty();
				assertThat(f.getInvoices()).isEmpty();
				assertThat(f.getAgreements()).isEmpty();
			})
			.satisfiesExactlyInAnyOrder(
				this::assertFacilityOne,
				this::assertFacilityTwo);
	}

	private void assertFacilityOne(Facility f) {
		assertThat(f.getAddress().getCareOf()).isEqualTo(TestFactory.IB1_CARE_OF);
		assertThat(f.getAddress().getCity()).isEqualTo(TestFactory.IB1_CITY);
		assertThat(f.getAddress().getPostalCode()).isEqualTo(TestFactory.IB1_POSTAL_CODE);
		assertThat(f.getAddress().getStreet()).isEqualTo(TestFactory.IB1_STREET);
		assertThat(f.getCommitmentEndDate()).isEqualTo(SDF.format(TestFactory.IB1_END_DATE));
		assertThat(f.getCommitmentStartDate()).isEqualTo(SDF.format(TestFactory.IB1_START_DATE));
		assertThat(f.getFacilityId()).isEqualTo(TestFactory.IB1_FACILITY_ID);
		assertThat(f.getLastModifiedDate()).isEqualTo(SDF.format(TestFactory.IB1_LAST_MODIFIED_DATE));
		assertThat(f.getPlacementId()).isEqualTo(TestFactory.IB1_PLACEMENT_ID);
		assertThat(f.getType()).isEqualTo(TestFactory.IB1_TYPE);
		assertThat(f.getInformation()).hasSize(1).satisfiesExactly(m -> {
			assertThat(m.getDisplayName()).isEqualTo(TestFactory.IB1_META_DISPLAY_NAME);
			assertThat(m.getName()).isEqualTo(TestFactory.IB1_META_KEY);
			assertThat(m.getType()).isEqualTo(TestFactory.IB1_META_TYPE);
			assertThat(m.getValue()).isEqualTo(TestFactory.IB1_META_VALUE);
		});
	}

	private void assertFacilityTwo(Facility f) {
		assertThat(f.getAddress().getCareOf()).isEqualTo(TestFactory.IB2_CARE_OF);
		assertThat(f.getAddress().getCity()).isEqualTo(TestFactory.IB2_CITY);
		assertThat(f.getAddress().getPostalCode()).isEqualTo(TestFactory.IB2_POSTAL_CODE);
		assertThat(f.getAddress().getStreet()).isEqualTo(TestFactory.IB2_STREET);
		assertThat(f.getCommitmentEndDate()).isNull();
		assertThat(f.getCommitmentStartDate()).isEqualTo(SDF.format(TestFactory.IB2_START_DATE));
		assertThat(f.getFacilityId()).isEqualTo(TestFactory.IB2_FACILITY_ID);
		assertThat(f.getLastModifiedDate()).isEqualTo(SDF.format(TestFactory.IB2_LAST_MODIFIED_DATE));
		assertThat(f.getPlacementId()).isEqualTo(TestFactory.IB2_PLACEMENT_ID);
		assertThat(f.getType()).isEqualTo(TestFactory.IB2_TYPE);
		assertThat(f.getInformation()).hasSize(1).satisfiesExactly(m -> {
			assertThat(m.getDisplayName()).isEqualTo(TestFactory.IB2_META_DISPLAY_NAME);
			assertThat(m.getName()).isEqualTo(TestFactory.IB2_META_KEY);
			assertThat(m.getType()).isEqualTo(TestFactory.IB2_META_TYPE);
			assertThat(m.getValue()).isEqualTo(TestFactory.IB2_META_VALUE);
		});
	}

	@Test
	void toInstalledBaseFromNull() {
		assertThat(IntricMapper.toInstalledBase(null)).isNull();
	}

	@Test
	void toInstalledBaseFromEmptyObject() {
		final var result = IntricMapper.toInstalledBase(new InstalledBaseCustomer());

		assertThat(result)
			.hasAllNullFieldsOrPropertiesExcept("facilities")
			.hasFieldOrPropertyWithValue("facilities", emptyList());
	}
}
