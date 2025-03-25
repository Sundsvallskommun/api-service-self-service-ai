package se.sundsvall.selfserviceai.integration.intric.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createAgreements;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;

import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;

class AgreementDecoratorTest {

	@Test
	void addAgreements() {
		// Arrange
		final var installedBase = IntricMapper.toIntricModel(Map.of("123456789", createCustomer()));
		final var agreements = new ArrayList<>(createAgreements(true));
		agreements.add(null);

		// Act
		AgreementDecorator.addAgreements(installedBase.getFacilities(), agreements);

		// Assert
		assertThat(installedBase.getFacilities()).satisfiesExactlyInAnyOrder(f -> {
			assertThat(f.getAgreements()).hasSize(2).satisfiesExactlyInAnyOrder(a -> {
				assertThat(a.getAgreementId()).isEqualTo(TestFactory.IB1_AGREEMENT1_ID);
				assertThat(a.isBound()).isEqualTo(TestFactory.IB1_AGREEMENT1_BINDING);
				assertThat(a.getBindingRule()).isEqualTo(TestFactory.IB1_AGREEMENT1_BINDING_RULE);
				assertThat(a.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT1_CATEGORY.name());
				assertThat(a.getDescription()).isEqualTo(TestFactory.IB1_AGREEMENT1_DESCRIPTION);
				assertThat(a.getFromDate()).isEqualTo(TestFactory.IB1_AGREEMENT1_START_DATE);
			}, a -> {
				assertThat(a.getAgreementId()).isEqualTo(TestFactory.IB1_AGREEMENT2_ID);
				assertThat(a.isBound()).isFalse();
				assertThat(a.getBindingRule()).isNull();
				assertThat(a.getCategory()).isEqualTo(TestFactory.IB1_AGREEMENT2_CATEGORY.name());
				assertThat(a.getDescription()).isEqualTo(TestFactory.IB1_AGREEMENT2_DESCRIPTION);
				assertThat(a.getFromDate()).isEqualTo(TestFactory.IB1_AGREEMENT2_START_DATE);
			});
		}, f -> {
			assertThat(f.getAgreements()).hasSize(1).satisfiesExactlyInAnyOrder(a -> {
				assertThat(a.getAgreementId()).isEqualTo(TestFactory.IB2_AGREEMENT1_ID);
				assertThat(a.isBound()).isFalse();
				assertThat(a.getBindingRule()).isNull();
				assertThat(a.getCategory()).isEqualTo(TestFactory.IB2_AGREEMENT1_CATEGORY.name());
				assertThat(a.getDescription()).isEqualTo(TestFactory.IB2_AGREEMENT1_DESCRIPTION);
				assertThat(a.getFromDate()).isEqualTo(TestFactory.IB2_AGREEMENT1_START_DATE);
			});
		});
	}

	@Test
	void addAgreementsNoMatches() {
		// Arrange
		final var agreements = createAgreements(false);
		final var installedBase = IntricMapper.toIntricModel(Map.of("123456789", createCustomer()));

		// Act
		AgreementDecorator.addAgreements(installedBase.getFacilities(), agreements);

		// Assert
		assertThat(installedBase.getFacilities()).flatExtracting(Facility::getAgreements).isEmpty();
	}

	@Test
	void addAgreementsFromNull() {
		// Arrange
		final var installedBase = IntricMapper.toIntricModel(Map.of("123456789", createCustomer()));

		// Act
		AgreementDecorator.addAgreements(installedBase.getFacilities(), null);

		// Assert
		assertThat(installedBase.getFacilities()).flatExtracting(Facility::getAgreements).isEmpty();
	}

}
