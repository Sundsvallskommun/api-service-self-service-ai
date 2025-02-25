package se.sundsvall.selfserviceai.service;

import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createAgreements;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;
import static se.sundsvall.selfserviceai.TestFactory.createInvoices;
import static se.sundsvall.selfserviceai.TestFactory.createMeasurements;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.service.model.Facility;
import se.sundsvall.selfserviceai.service.model.Invoice;

@ExtendWith(ResourceLoaderExtension.class)
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
		System.out.println(result);
		assertThat(result.getFacilities()).hasSize(2)
			.allSatisfy(f -> {
				assertThat(f.getMeasurements()).isEmpty();
				assertThat(f.getInvoices()).isEmpty();
				assertThat(f.getAgreements()).isEmpty();
			})
			.satisfiesExactlyInAnyOrder(f -> {
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
			}, f -> {
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

	@Test
	void addAgreements() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var agreements = new ArrayList<>(createAgreements(true));
		agreements.add(null);

		// Act
		final var result = IntricMapper.addAgreements(installedBase, agreements);

		// Assert
		assertThat(result.getFacilities()).satisfiesExactlyInAnyOrder(f -> {
			assertThat(f.getAgreements()).hasSize(2).satisfiesExactlyInAnyOrder(a -> {
				assertThat(a.getAgreementId()).isEqualTo(TestFactory.IB1_AGREEMENT1_ID);
				assertThat(a.isBound()).isEqualTo(TestFactory.IB1_AGREEMENT1_BINDING);
				assertThat(a.getBindingRule()).isEqualTo(TestFactory.IB1_AGREEMENT1_BINDING_RULE);
				assertThat(a.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_CATEGORY.name());
				assertThat(a.getDescription()).isEqualTo(TestFactory.IB1_AGREEMENT1_DESCRIPTION);
				assertThat(a.getFromDate()).isEqualTo(SDF.format(TestFactory.IB1_AGREEMENT1_START_DATE));
			}, a -> {
				assertThat(a.getAgreementId()).isEqualTo(TestFactory.IB1_AGREEMENT2_ID);
				assertThat(a.isBound()).isFalse();
				assertThat(a.getBindingRule()).isNull();
				assertThat(a.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT2_CATEGORY.name());
				assertThat(a.getDescription()).isEqualTo(TestFactory.IB1_AGREEMENT2_DESCRIPTION);
				assertThat(a.getFromDate()).isEqualTo(SDF.format(TestFactory.IB1_AGREEMENT2_START_DATE));
			});
		}, f -> {
			assertThat(f.getAgreements()).hasSize(1).satisfiesExactlyInAnyOrder(a -> {
				assertThat(a.getAgreementId()).isEqualTo(TestFactory.IB2_AGREEMENT1_ID);
				assertThat(a.isBound()).isFalse();
				assertThat(a.getBindingRule()).isNull();
				assertThat(a.getCategory()).isEqualTo(TestFactory.IB2_AGREEMENT1_CATEGORY.name());
				assertThat(a.getDescription()).isEqualTo(TestFactory.IB2_AGREEMENT1_DESCRIPTION);
				assertThat(a.getFromDate()).isEqualTo(SDF.format(TestFactory.IB2_AGREEMENT1_START_DATE));
			});
		});
	}

	@Test
	void addAgreementsNoMatches() {
		// Arrange
		final var agreements = createAgreements(false);
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());

		// Act
		final var result = IntricMapper.addAgreements(installedBase, agreements);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getAgreements).isEmpty();
	}

	@Test
	void addAgreementsFromNull() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());

		// Act
		final var result = IntricMapper.addAgreements(installedBase, null);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getAgreements).isEmpty();
	}

	@Test
	void addMeasurements() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var measurements = new ArrayList<>(createMeasurements(true));
		measurements.add(null);

		// Act
		final var result = IntricMapper.addMeasurements(installedBase, measurements);

		// Assert
		assertThat(result.getFacilities()).satisfiesExactlyInAnyOrder(f -> {
			assertThat(f.getMeasurements()).hasSize(2).satisfiesExactlyInAnyOrder(m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT1_VALUE.setScale(2, RoundingMode.HALF_DOWN));
			}, m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB1_AGREEMENT1_MEASUREMENT2_VALUE.setScale(2, RoundingMode.HALF_DOWN));
			});
		}, f -> {
			assertThat(f.getMeasurements()).hasSize(1).satisfiesExactlyInAnyOrder(m -> {
				assertThat(m.getCategory()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_CATEGORY.name());
				assertThat(m.getMeasurementType()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_TYPE);
				assertThat(m.getTimestamp()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_TIMESTAMP.toInstant().atOffset(OffsetDateTime.now(systemDefault()).getOffset()));
				assertThat(m.getUnit()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_UNIT);
				assertThat(m.getValue()).isEqualTo(TestFactory.IB2_AGREEMENT1_MEASUREMENT1_VALUE.setScale(2, RoundingMode.HALF_DOWN));
			});
		});
	}

	@Test
	void addMeasurementsNoMatches() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var measurements = new ArrayList<>(createMeasurements(false));

		// Act
		final var result = IntricMapper.addMeasurements(installedBase, measurements);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getMeasurements).isEmpty();
	}

	@Test
	void addMeasurementsFromNull() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());

		// Act
		final var result = IntricMapper.addMeasurements(installedBase, null);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getMeasurements).isEmpty();
	}

	@Test
	void addInvoices() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var invoices = new ArrayList<>(createInvoices(true));
		invoices.add(null);

		// Act
		final var result = IntricMapper.addInvoices(installedBase, invoices);

		// Assert
		assertThat(result.getFacilities()).satisfiesExactlyInAnyOrder(f -> {
			assertThat(f.getInvoices()).hasSize(2).satisfiesExactlyInAnyOrder(i -> {
				assertInvoiceWithAddress(i);
			}, i -> {
				assertInvoiceWithoutAddress(i);
			});
		}, f -> {
			assertThat(f.getInvoices()).isEmpty();
		});
	}

	private void assertInvoiceWithoutAddress(Invoice i) {
		assertThat(i.getAmountVatExcluded()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_AMOUNT_VAT_EXCLUDED));
		assertThat(i.getAmountVatIncluded()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_AMOUNT_VAT_INCLUDED));
		assertThat(i.getCurrency()).isEqualTo(TestFactory.IB1_INVOICE2_CURRENCY);
		assertThat(i.getDescription()).isEqualTo(TestFactory.IB1_INVOICE2_DESCRIPTION);
		assertThat(i.getDueDate()).isEqualTo(SDF.format(TestFactory.IB1_INVOICE2_DUE_DATE));
		assertThat(i.getInvoiceAddress()).isNull();
		assertThat(i.getInvoiceName()).isEqualTo(TestFactory.IB1_INVOICE2_NAME);
		assertThat(i.getInvoiceNumber()).isEqualTo(TestFactory.IB1_INVOICE2_NUMBER);
		assertThat(i.getInvoiceType()).isEqualTo(TestFactory.IB1_INVOICE2_TYPE.name());
		assertThat(i.getInvoicingDate()).isEqualTo(SDF.format(TestFactory.IB1_INVOICE2_DATE));
		assertThat(i.getOcrNumber()).isEqualTo(TestFactory.IB1_INVOICE2_OCR_NUMBER);
		assertThat(i.getOrganizationNumber()).isEqualTo(TestFactory.IB1_INVOICE2_ORGANIZATION_NUMBER);
		assertThat(i.getRounding()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_ROUNDING));
		assertThat(i.getStatus()).isEqualTo(TestFactory.IB1_INVOICE2_STATUS.name());
		assertThat(i.getTotalAmount()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_TOTAL_AMOUNT));
		assertThat(i.getVat()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_VAT));
		assertThat(i.getVatEligibleAmount()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_VAT_ELIGABLE_AMOUNT));
	}

	private void assertInvoiceWithAddress(Invoice i) {
		assertThat(i.getAmountVatExcluded()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_AMOUNT_VAT_EXCLUDED));
		assertThat(i.getAmountVatIncluded()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_AMOUNT_VAT_INCLUDED));
		assertThat(i.getCurrency()).isEqualTo(TestFactory.IB1_INVOICE1_CURRENCY);
		assertThat(i.getDescription()).isEqualTo(TestFactory.IB1_INVOICE1_DESCRIPTION);
		assertThat(i.getDueDate()).isEqualTo(SDF.format(TestFactory.IB1_INVOICE1_DUE_DATE));
		assertThat(i.getInvoiceAddress()).isNotNull().satisfies(a -> {
			assertThat(a.getCareOf()).isEqualTo(TestFactory.IB1_INVOICE1_CARE_OF);
			assertThat(a.getCity()).isEqualTo(TestFactory.IB1_INVOICE1_CITY);
			assertThat(a.getPostalCode()).isEqualTo(TestFactory.IB1_INVOICE1_POSTAL_CODE);
			assertThat(a.getStreet()).isEqualTo(TestFactory.IB1_INVOICE1_STREET);
		});
		assertThat(i.getInvoiceName()).isEqualTo(TestFactory.IB1_INVOICE1_NAME);
		assertThat(i.getInvoiceNumber()).isEqualTo(TestFactory.IB1_INVOICE1_NUMBER);
		assertThat(i.getInvoiceType()).isEqualTo(TestFactory.IB1_INVOICE1_TYPE.name());
		assertThat(i.getInvoicingDate()).isEqualTo(SDF.format(TestFactory.IB1_INVOICE1_DATE));
		assertThat(i.getOcrNumber()).isEqualTo(TestFactory.IB1_INVOICE1_OCR_NUMBER);
		assertThat(i.getOrganizationNumber()).isEqualTo(TestFactory.IB1_INVOICE1_ORGANIZATION_NUMBER);
		assertThat(i.getRounding()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_ROUNDING));
		assertThat(i.getStatus()).isEqualTo(TestFactory.IB1_INVOICE1_STATUS.name());
		assertThat(i.getTotalAmount()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_TOTAL_AMOUNT));
		assertThat(i.getVat()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_VAT));
		assertThat(i.getVatEligibleAmount()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_VAT_ELIGABLE_AMOUNT));
	}

	@Test
	void addInvoicesNoMatches() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var invoices = new ArrayList<>(createInvoices(false));

		// Act
		final var result = IntricMapper.addInvoices(installedBase, invoices);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getInvoices).isEmpty();
	}

	@Test
	void addInvoicesFromNull() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());

		// Act
		final var result = IntricMapper.addInvoices(installedBase, null);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getInvoices).isEmpty();
	}

	@Test
	void toJson(@Load(value = "junit/expected-structure.json", as = Load.ResourceType.STRING) String expected) throws Exception {
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var agreements = createAgreements(true);
		final var measurements = new ArrayList<>(createMeasurements(true));
		final var invoices = new ArrayList<>(createInvoices(true));

		IntricMapper.addAgreements(installedBase, agreements);
		IntricMapper.addMeasurements(installedBase, measurements);
		IntricMapper.addInvoices(installedBase, invoices);

		assertThat(IntricMapper.toJsonString(installedBase)).isEqualToIgnoringWhitespace(expected);
	}
}
