package se.sundsvall.selfserviceai.integration.intric.model.filecontent;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InvoiceTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(Invoice.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethodsForInvoice() {
		final var amountVatExcluded = BigDecimal.valueOf(123);
		final var amountVatIncluded = BigDecimal.valueOf(234);
		final var currency = "currency";
		final var description = "description";
		final var dueDate = LocalDate.now();
		final var invoiceAddress = Address.builder().build();
		final var invoiceName = "invoiceName";
		final var invoiceNumber = "invoiceNumber";
		final var invoiceType = "invoiceType";
		final var invoicingDate = LocalDate.now().minusDays(1);
		final var ocrNumber = "ocrNumber";
		final var organizationNumber = "organizationNumber";
		final var pdfAvailable = true;
		final var reversedVat = true;
		final var rounding = BigDecimal.valueOf(567);
		final var status = "status";
		final var totalAmount = BigDecimal.valueOf(678);
		final var vat = BigDecimal.valueOf(890);
		final var vatEligibleAmount = BigDecimal.valueOf(901);

		final var bean = Invoice.builder()
			.withAmountVatExcluded(amountVatExcluded)
			.withAmountVatIncluded(amountVatIncluded)
			.withCurrency(currency)
			.withDescription(description)
			.withDueDate(dueDate)
			.withInvoiceAddress(invoiceAddress)
			.withInvoiceName(invoiceName)
			.withInvoiceNumber(invoiceNumber)
			.withInvoiceType(invoiceType)
			.withInvoicingDate(invoicingDate)
			.withOcrNumber(ocrNumber)
			.withOrganizationNumber(organizationNumber)
			.withPdfAvailable(pdfAvailable)
			.withReversedVat(reversedVat)
			.withRounding(rounding)
			.withStatus(status)
			.withTotalAmount(totalAmount)
			.withVat(vat)
			.withVatEligibleAmount(vatEligibleAmount)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getAmountVatExcluded()).isEqualTo(amountVatExcluded);
		assertThat(bean.getAmountVatIncluded()).isEqualByComparingTo(amountVatIncluded);
		assertThat(bean.getCurrency()).isEqualTo(currency);
		assertThat(bean.getDescription()).isEqualTo(description);
		assertThat(bean.getDueDate()).isEqualTo(dueDate);
		assertThat(bean.getInvoiceAddress()).isEqualTo(invoiceAddress);
		assertThat(bean.getInvoiceName()).isEqualTo(invoiceName);
		assertThat(bean.getInvoiceNumber()).isEqualTo(invoiceNumber);
		assertThat(bean.getInvoiceType()).isEqualTo(invoiceType);
		assertThat(bean.getInvoicingDate()).isEqualTo(invoicingDate);
		assertThat(bean.getOcrNumber()).isEqualTo(ocrNumber);
		assertThat(bean.getOrganizationNumber()).isEqualTo(organizationNumber);
		assertThat(bean.getRounding()).isEqualTo(rounding);
		assertThat(bean.getStatus()).isEqualTo(status);
		assertThat(bean.getTotalAmount()).isEqualByComparingTo(totalAmount);
		assertThat(bean.getVat()).isEqualTo(vat);
		assertThat(bean.getVatEligibleAmount()).isEqualTo(vatEligibleAmount);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Invoice.builder().build())
			.hasAllNullFieldsOrPropertiesExcept("pdfAvailable", "reversedVat")
			.hasFieldOrPropertyWithValue("pdfAvailable", false)
			.hasFieldOrPropertyWithValue("reversedVat", false);
		assertThat(new Invoice())
			.hasAllNullFieldsOrPropertiesExcept("pdfAvailable", "reversedVat")
			.hasFieldOrPropertyWithValue("pdfAvailable", false)
			.hasFieldOrPropertyWithValue("reversedVat", false);
	}
}
