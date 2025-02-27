package se.sundsvall.selfserviceai.integration.intric.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createAgreements;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;
import static se.sundsvall.selfserviceai.TestFactory.createInvoices;
import static se.sundsvall.selfserviceai.TestFactory.createMeasurements;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;

@ExtendWith(ResourceLoaderExtension.class)
class JsonBuilderTest {

	@Test
	void toJson(@Load(value = "junit/expected-structure.json", as = Load.ResourceType.STRING) String expected) throws Exception {
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());

		AgreementDecorator.addAgreements(installedBase, createAgreements(true));
		MeasurementDecorator.addMeasurements(installedBase, createMeasurements(true));
		InvoiceDecorator.addInvoices(installedBase, createInvoices(true));

		assertThat(JsonBuilder.toJsonString(installedBase)).isEqualToIgnoringWhitespace(expected);
	}
}
