package se.sundsvall.selfserviceai.integration.intric.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;
import static se.sundsvall.selfserviceai.TestFactory.createInvoices;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import se.sundsvall.selfserviceai.TestFactory;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Invoice;

class InvoiceDecoratorTest {

	@Test
	void addInvoices() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var invoices = new ArrayList<>(createInvoices(true));
		invoices.add(null);

		// Act
		final var result = InvoiceDecorator.addInvoices(installedBase, invoices);

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
		assertThat(i.getDueDate()).isEqualTo(TestFactory.IB1_INVOICE2_DUE_DATE);
		assertThat(i.getInvoiceAddress()).isNull();
		assertThat(i.getInvoiceName()).isEqualTo(TestFactory.IB1_INVOICE2_NAME);
		assertThat(i.getInvoiceNumber()).isEqualTo(TestFactory.IB1_INVOICE2_NUMBER);
		assertThat(i.getInvoiceType()).isEqualTo(TestFactory.IB1_INVOICE2_TYPE.name());
		assertThat(i.getInvoicingDate()).isEqualTo(TestFactory.IB1_INVOICE2_DATE);
		assertThat(i.getOcrNumber()).isEqualTo(TestFactory.IB1_INVOICE2_OCR_NUMBER);
		assertThat(i.getOrganizationNumber()).isEqualTo(TestFactory.IB1_INVOICE2_ORGANIZATION_NUMBER);
		assertThat(i.getRounding()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_ROUNDING));
		assertThat(i.getStatus()).isEqualTo(TestFactory.IB1_INVOICE2_STATUS.name());
		assertThat(i.getTotalAmount()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_TOTAL_AMOUNT));
		assertThat(i.getVat()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_VAT));
		assertThat(i.getVatEligibleAmount()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE2_VAT_ELIGIBLE_AMOUNT));
	}

	private void assertInvoiceWithAddress(Invoice i) {
		assertThat(i.getAmountVatExcluded()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_AMOUNT_VAT_EXCLUDED));
		assertThat(i.getAmountVatIncluded()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_AMOUNT_VAT_INCLUDED));
		assertThat(i.getCurrency()).isEqualTo(TestFactory.IB1_INVOICE1_CURRENCY);
		assertThat(i.getDescription()).isEqualTo(TestFactory.IB1_INVOICE1_DESCRIPTION);
		assertThat(i.getDueDate()).isEqualTo(TestFactory.IB1_INVOICE1_DUE_DATE);
		assertThat(i.getInvoiceAddress()).isNotNull().satisfies(a -> {
			assertThat(a.getCareOf()).isEqualTo(TestFactory.IB1_INVOICE1_CARE_OF);
			assertThat(a.getCity()).isEqualTo(TestFactory.IB1_INVOICE1_CITY);
			assertThat(a.getPostalCode()).isEqualTo(TestFactory.IB1_INVOICE1_POSTAL_CODE);
			assertThat(a.getStreet()).isEqualTo(TestFactory.IB1_INVOICE1_STREET);
		});
		assertThat(i.getInvoiceName()).isEqualTo(TestFactory.IB1_INVOICE1_NAME);
		assertThat(i.getInvoiceNumber()).isEqualTo(TestFactory.IB1_INVOICE1_NUMBER);
		assertThat(i.getInvoiceType()).isEqualTo(TestFactory.IB1_INVOICE1_TYPE.name());
		assertThat(i.getInvoicingDate()).isEqualTo(TestFactory.IB1_INVOICE1_DATE);
		assertThat(i.getOcrNumber()).isEqualTo(TestFactory.IB1_INVOICE1_OCR_NUMBER);
		assertThat(i.getOrganizationNumber()).isEqualTo(TestFactory.IB1_INVOICE1_ORGANIZATION_NUMBER);
		assertThat(i.getRounding()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_ROUNDING));
		assertThat(i.getStatus()).isEqualTo(TestFactory.IB1_INVOICE1_STATUS.name());
		assertThat(i.getTotalAmount()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_TOTAL_AMOUNT));
		assertThat(i.getVat()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_VAT));
		assertThat(i.getVatEligibleAmount()).isEqualTo(new BigDecimal(TestFactory.IB1_INVOICE1_VAT_ELIGIBLE_AMOUNT));
	}

	@Test
	void addInvoicesNoMatches() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());
		final var invoices = new ArrayList<>(createInvoices(false));

		// Act
		final var result = InvoiceDecorator.addInvoices(installedBase, invoices);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getInvoices).isEmpty();
	}

	@Test
	void addInvoicesFromNull() {
		// Arrange
		final var installedBase = IntricMapper.toInstalledBase(createCustomer());

		// Act
		final var result = InvoiceDecorator.addInvoices(installedBase, null);

		// Assert
		assertThat(result.getFacilities()).flatExtracting(Facility::getInvoices).isEmpty();
	}
}
