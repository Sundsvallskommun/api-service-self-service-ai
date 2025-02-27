package se.sundsvall.selfserviceai.integration.intric.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createAgreements;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;

class AgreementDecoratorTest {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

	@Test
	void addAgreements() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var agreements = new ArrayList<>(createAgreements(true));
		agreements.add(null);

		// Act
		final var result = AgreementDecorator.addAgreements(installedBase, agreements);

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
		final var result = AgreementDecorator.addAgreements(installedBase, agreements);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getAgreements).isEmpty();
	}

	@Test
	void addAgreementsFromNull() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());

		// Act
		final var result = AgreementDecorator.addAgreements(installedBase, null);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getAgreements).isEmpty();
	}

}
