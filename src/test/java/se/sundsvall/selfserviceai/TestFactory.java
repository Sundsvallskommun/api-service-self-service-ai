package se.sundsvall.selfserviceai;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.agreement.Category;
import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseItemAddress;
import generated.se.sundsvall.installedbase.InstalledBaseItemMetaData;
import generated.se.sundsvall.invoices.Address;
import generated.se.sundsvall.invoices.Invoice;
import generated.se.sundsvall.invoices.InvoiceStatus;
import generated.se.sundsvall.invoices.InvoiceType;
import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementPoints;
import generated.se.sundsvall.measurementdata.MeasurementSerie;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public class TestFactory {
	private TestFactory() {}

	private static final String NO_MATCH = "NO_MATCH";

	public static final String CUSTOMER_NUMBER = "123";
	public static final String PARTY_ID = "8e46e6be-eaaf-40dd-b5c5-be3dbe858999";

	public static final String IB1_FACILITY_ID = "facilityId1";
	public static final int IB1_PLACEMENT_ID = 111;
	public static final String IB1_TYPE = "type1";
	public static final String IB1_CARE_OF = "careOf1";
	public static final String IB1_CITY = "city1";
	public static final String IB1_POSTAL_CODE = "postalCode1";
	public static final String IB1_STREET = "street1";
	public static final String IB1_META_DISPLAY_NAME = "displayName1";
	public static final String IB1_META_KEY = "key1";
	public static final String IB1_META_TYPE = "metaType1";
	public static final String IB1_META_VALUE = "value1";
	public static final LocalDate IB1_END_DATE = LocalDate.of(2025, 1, 10);
	public static final LocalDate IB1_START_DATE = LocalDate.of(2020, 1, 10);
	public static final LocalDate IB1_LAST_MODIFIED_DATE = LocalDate.of(2024, 1, 9);
	public static final String IB1_AGREEMENT1_ID = "agreementId1";
	public static final LocalDate IB1_AGREEMENT1_START_DATE = LocalDate.of(2020, 1, 11).plusWeeks(1);
	public static final String IB1_AGREEMENT1_BILLING_ID = "billingId1";
	public static final Boolean IB1_AGREEMENT1_BINDING = true;
	public static final String IB1_AGREEMENT1_BINDING_RULE = "bindingRule1";
	public static final String IB1_AGREEMENT1_DESCRIPTION = "description1";
	public static final Category IB1_AGREEMENT1_CATEGORY = Category.ELECTRICITY;
	public static final String IB1_AGREEMENT2_ID = "agreementId2";
	public static final LocalDate IB1_AGREEMENT2_START_DATE = LocalDate.of(2020, 1, 12);
	public static final String IB1_AGREEMENT2_BILLING_ID = "billingId2";
	public static final String IB1_AGREEMENT2_DESCRIPTION = "description2";
	public static final Category IB1_AGREEMENT2_CATEGORY = Category.ELECTRICITY_TRADE;
	public static final generated.se.sundsvall.measurementdata.Category IB1_AGREEMENT1_MEASUREMENT1_CATEGORY = generated.se.sundsvall.measurementdata.Category.ELECTRICITY;
	public static final String IB1_AGREEMENT1_MEASUREMENT1_TYPE = "measurementType1";
	public static final String IB1_AGREEMENT1_MEASUREMENT1_UNIT = "unit1";
	public static final OffsetDateTime IB1_AGREEMENT1_MEASUREMENT1_TIMESTAMP = LocalDate.of(2020, 1, 17).atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime();
	public static final BigDecimal IB1_AGREEMENT1_MEASUREMENT1_VALUE = new BigDecimal("123.456789");
	public static final generated.se.sundsvall.measurementdata.Category IB1_AGREEMENT1_MEASUREMENT2_CATEGORY = generated.se.sundsvall.measurementdata.Category.ELECTRICITY;
	public static final String IB1_AGREEMENT1_MEASUREMENT2_TYPE = "measurementType2";
	public static final String IB1_AGREEMENT1_MEASUREMENT2_UNIT = "unit2";
	public static final OffsetDateTime IB1_AGREEMENT1_MEASUREMENT2_TIMESTAMP = LocalDate.of(2024, 2, 15).atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime();
	public static final BigDecimal IB1_AGREEMENT1_MEASUREMENT2_VALUE = new BigDecimal("234.567890");
	public static final String IB1_INVOICE1_AMOUNT_VAT_EXCLUDED = "1000.00";
	public static final String IB1_INVOICE1_AMOUNT_VAT_INCLUDED = "1250.00";
	public static final String IB1_INVOICE1_CURRENCY = "currency1";
	public static final String IB1_INVOICE1_CARE_OF = "invoiceCareOf1";
	public static final String IB1_INVOICE1_CITY = "invoiceCity1";
	public static final String IB1_INVOICE1_POSTAL_CODE = "invoicePostalCode1";
	public static final String IB1_INVOICE1_STREET = "invoiceStreet1";
	public static final LocalDate IB1_INVOICE1_DATE = LocalDate.of(2020, 2, 1);
	public static final LocalDate IB1_INVOICE1_DUE_DATE = LocalDate.of(2020, 2, 28);
	public static final String IB1_INVOICE2_AMOUNT_VAT_EXCLUDED = "2500.00";
	public static final String IB1_INVOICE2_AMOUNT_VAT_INCLUDED = "3000.00";
	public static final String IB1_INVOICE2_CURRENCY = "currency2";
	public static final LocalDate IB1_INVOICE2_DUE_DATE = LocalDate.of(2024, 2, 13);
	public static final LocalDate IB1_INVOICE2_DATE = LocalDate.of(2024, 2, 28);
	public static final String IB1_INVOICE1_DESCRIPTION = "invoiceDescription1";
	public static final String IB1_INVOICE1_NAME = "invoiceName1";
	public static final String IB1_INVOICE1_NUMBER = "invoiceNumber1";
	public static final InvoiceStatus IB1_INVOICE1_STATUS = InvoiceStatus.SENT;
	public static final InvoiceType IB1_INVOICE1_TYPE = InvoiceType.INVOICE;
	public static final String IB1_INVOICE1_OCR_NUMBER = "invoiceOcrNumber1";
	public static final String IB1_INVOICE1_ORGANIZATION_NUMBER = "organizationNumber1";
	public static final Boolean IB1_INVOICE1_PDF_AVAILABLE = true;
	public static final Boolean IB1_INVOICE1_REVERSED_VAT = false;
	public static final String IB1_INVOICE1_ROUNDING = "-0.35";
	public static final String IB1_INVOICE1_TOTAL_AMOUNT = "1250.35";
	public static final String IB1_INVOICE1_VAT = "0.25";
	public static final String IB1_INVOICE1_VAT_ELIGIBLE_AMOUNT = "456.78";
	public static final String IB1_INVOICE2_DESCRIPTION = "invoiceDescription2";
	public static final String IB1_INVOICE2_NAME = "invoiceName2";
	public static final String IB1_INVOICE2_NUMBER = "invoiceNumber2";
	public static final InvoiceStatus IB1_INVOICE2_STATUS = InvoiceStatus.REMINDER;
	public static final InvoiceType IB1_INVOICE2_TYPE = InvoiceType.CONSOLIDATED_INVOICE;
	public static final String IB1_INVOICE2_OCR_NUMBER = "invoiceOcrNumber2";
	public static final String IB1_INVOICE2_ORGANIZATION_NUMBER = "organizationNumber2";
	public static final Boolean IB1_INVOICE2_REVERSED_VAT = true;
	public static final String IB1_INVOICE2_ROUNDING = "0.49";
	public static final String IB1_INVOICE2_TOTAL_AMOUNT = "2999.51";
	public static final String IB1_INVOICE2_VAT = "0.25";
	public static final String IB1_INVOICE2_VAT_ELIGIBLE_AMOUNT = "123.65";

	public static final String IB2_FACILITY_ID = "facilityId2";
	public static final int IB2_PLACEMENT_ID = 222;
	public static final String IB2_TYPE = "type2";
	public static final String IB2_CARE_OF = "careOf2";
	public static final String IB2_CITY = "city2";
	public static final String IB2_POSTAL_CODE = "postalCode2";
	public static final String IB2_STREET = "street2";
	public static final String IB2_META_DISPLAY_NAME = "displayName2";
	public static final String IB2_META_KEY = "key2";
	public static final String IB2_META_TYPE = "metaType2";
	public static final String IB2_META_VALUE = "value2";
	public static final LocalDate IB2_START_DATE = LocalDate.of(2021, 5, 15);
	public static final LocalDate IB2_LAST_MODIFIED_DATE = LocalDate.of(2024, 6, 30);
	public static final String IB2_AGREEMENT1_ID = "agreementId3";
	public static final LocalDate IB2_AGREEMENT1_START_DATE = LocalDate.of(2024, 5, 24);
	public static final String IB2_AGREEMENT1_BILLING_ID = "billingId3";
	public static final String IB2_AGREEMENT1_DESCRIPTION = "description3";
	public static final Category IB2_AGREEMENT1_CATEGORY = Category.WASTE_MANAGEMENT;
	public static final generated.se.sundsvall.measurementdata.Category IB2_AGREEMENT1_MEASUREMENT1_CATEGORY = generated.se.sundsvall.measurementdata.Category.WASTE_MANAGEMENT;
	public static final String IB2_AGREEMENT1_MEASUREMENT1_TYPE = "measurementTYpe3";
	public static final String IB2_AGREEMENT1_MEASUREMENT1_UNIT = "unit3";
	public static final OffsetDateTime IB2_AGREEMENT1_MEASUREMENT1_TIMESTAMP = LocalDate.of(2024, 6, 15).atStartOfDay().minusHours(1).atZone(ZoneId.systemDefault()).toOffsetDateTime();
	public static final BigDecimal IB2_AGREEMENT1_MEASUREMENT1_VALUE = new BigDecimal("500");

	public static InstalledBaseCustomer createCustomer() {
		return new InstalledBaseCustomer()
			.customerNumber(CUSTOMER_NUMBER)
			.items(List.of(
				createInstalledBaseItem(1),
				createInstalledBaseItem(2)))
			.partyId(PARTY_ID);
	}

	private static InstalledBaseItem createInstalledBaseItem(int baseItem) {
		return new InstalledBaseItem()
			.address(createInstalledBaseItemAddress(baseItem))
			.facilityCommitmentEndDate(baseItem == 1 ? IB1_END_DATE : null)
			.facilityCommitmentStartDate(baseItem == 1 ? IB1_START_DATE : IB2_START_DATE)
			.facilityId(baseItem == 1 ? IB1_FACILITY_ID : IB2_FACILITY_ID)
			.lastModifiedDate(baseItem == 1 ? IB1_LAST_MODIFIED_DATE : IB2_LAST_MODIFIED_DATE)
			.metaData(List.of(createMetadata(baseItem)))
			.placementId(baseItem == 1 ? IB1_PLACEMENT_ID : IB2_PLACEMENT_ID)
			.type(baseItem == 1 ? IB1_TYPE : IB2_TYPE);
	}

	private static InstalledBaseItemMetaData createMetadata(int baseItem) {
		return new InstalledBaseItemMetaData()
			.displayName(baseItem == 1 ? IB1_META_DISPLAY_NAME : IB2_META_DISPLAY_NAME)
			.key(baseItem == 1 ? IB1_META_KEY : IB2_META_KEY)
			.type(baseItem == 1 ? IB1_META_TYPE : IB2_META_TYPE)
			.value(baseItem == 1 ? IB1_META_VALUE : IB2_META_VALUE);
	}

	private static InstalledBaseItemAddress createInstalledBaseItemAddress(int baseItem) {
		return new InstalledBaseItemAddress()
			.careOf(baseItem == 1 ? IB1_CARE_OF : IB2_CARE_OF)
			.city(baseItem == 1 ? IB1_CITY : IB2_CITY)
			.postalCode(baseItem == 1 ? IB1_POSTAL_CODE : IB2_POSTAL_CODE)
			.street(baseItem == 1 ? IB1_STREET : IB2_STREET);
	}

	public static List<Agreement> createAgreements(boolean containsMatches) {
		return containsMatches ? List.of(
			new Agreement()
				.agreementId(IB1_AGREEMENT1_ID)
				.billingId(IB1_AGREEMENT1_BILLING_ID)
				.binding(IB1_AGREEMENT1_BINDING)
				.bindingRule(IB1_AGREEMENT1_BINDING_RULE)
				.category(IB1_AGREEMENT1_CATEGORY)
				.description(IB1_AGREEMENT1_DESCRIPTION)
				.facilityId(IB1_FACILITY_ID)
				.fromDate(IB1_AGREEMENT1_START_DATE),
			new Agreement()
				.agreementId(IB1_AGREEMENT2_ID)
				.billingId(IB1_AGREEMENT2_BILLING_ID)
				.category(IB1_AGREEMENT2_CATEGORY)
				.description(IB1_AGREEMENT2_DESCRIPTION)
				.facilityId(IB1_FACILITY_ID)
				.fromDate(IB1_AGREEMENT2_START_DATE),
			new Agreement()
				.agreementId(IB2_AGREEMENT1_ID)
				.billingId(IB2_AGREEMENT1_BILLING_ID)
				.category(IB2_AGREEMENT1_CATEGORY)
				.description(IB2_AGREEMENT1_DESCRIPTION)
				.facilityId(IB2_FACILITY_ID)
				.fromDate(IB2_AGREEMENT1_START_DATE))
			: List.of(new Agreement()
				.facilityId(NO_MATCH));
	}

	public static List<Data> createMeasurements(boolean containsMatches) {
		return containsMatches ? List.of(
			new Data()
				.facilityId(IB1_FACILITY_ID)
				.category(IB1_AGREEMENT1_MEASUREMENT1_CATEGORY)
				.addMeasurementSeriesItem(new MeasurementSerie()
					.measurementType(IB1_AGREEMENT1_MEASUREMENT1_TYPE)
					.unit(IB1_AGREEMENT1_MEASUREMENT1_UNIT)
					.addMeasurementPointsItem(new MeasurementPoints()
						.timestamp(IB1_AGREEMENT1_MEASUREMENT1_TIMESTAMP)
						.value(IB1_AGREEMENT1_MEASUREMENT1_VALUE))),
			new Data()
				.facilityId(IB1_FACILITY_ID)
				.category(IB1_AGREEMENT1_MEASUREMENT2_CATEGORY)
				.addMeasurementSeriesItem(new MeasurementSerie()
					.measurementType(IB1_AGREEMENT1_MEASUREMENT2_TYPE)
					.unit(IB1_AGREEMENT1_MEASUREMENT2_UNIT)
					.addMeasurementPointsItem(new MeasurementPoints()
						.timestamp(IB1_AGREEMENT1_MEASUREMENT2_TIMESTAMP)
						.value(IB1_AGREEMENT1_MEASUREMENT2_VALUE))),
			new Data()
				.facilityId(IB2_FACILITY_ID)
				.category(IB2_AGREEMENT1_MEASUREMENT1_CATEGORY)
				.addMeasurementSeriesItem(new MeasurementSerie()
					.measurementType(IB2_AGREEMENT1_MEASUREMENT1_TYPE)
					.unit(IB2_AGREEMENT1_MEASUREMENT1_UNIT)
					.addMeasurementPointsItem(new MeasurementPoints()
						.timestamp(IB2_AGREEMENT1_MEASUREMENT1_TIMESTAMP)
						.value(IB2_AGREEMENT1_MEASUREMENT1_VALUE))))
			: List.of(new Data()
				.facilityId(NO_MATCH));
	}

	public static List<Invoice> createInvoices(boolean containsMatches) {
		return containsMatches ? List.of(
			new Invoice()
				.facilityId(IB1_FACILITY_ID)
				.amountVatExcluded(Float.valueOf(IB1_INVOICE1_AMOUNT_VAT_EXCLUDED))
				.amountVatIncluded(Float.valueOf(IB1_INVOICE1_AMOUNT_VAT_INCLUDED))
				.currency(IB1_INVOICE1_CURRENCY)
				.dueDate(IB1_INVOICE1_DUE_DATE)
				.invoiceDate(IB1_INVOICE1_DATE)
				.invoiceAddress(new Address()
					.careOf(IB1_INVOICE1_CARE_OF)
					.city(IB1_INVOICE1_CITY)
					.postcode(IB1_INVOICE1_POSTAL_CODE)
					.street(IB1_INVOICE1_STREET))
				.invoiceDescription(IB1_INVOICE1_DESCRIPTION)
				.invoiceName(IB1_INVOICE1_NAME)
				.invoiceNumber(IB1_INVOICE1_NUMBER)
				.invoiceStatus(IB1_INVOICE1_STATUS)
				.invoiceType(IB1_INVOICE1_TYPE)
				.ocrNumber(IB1_INVOICE1_OCR_NUMBER)
				.organizationNumber(IB1_INVOICE1_ORGANIZATION_NUMBER)
				.pdfAvailable(IB1_INVOICE1_PDF_AVAILABLE)
				.reversedVat(IB1_INVOICE1_REVERSED_VAT)
				.rounding(Float.valueOf(IB1_INVOICE1_ROUNDING))
				.totalAmount(Float.valueOf(IB1_INVOICE1_TOTAL_AMOUNT))
				.vat(Float.valueOf(IB1_INVOICE1_VAT))
				.vatEligibleAmount(Float.valueOf(IB1_INVOICE1_VAT_ELIGIBLE_AMOUNT)),
			new Invoice()
				.facilityId(IB1_FACILITY_ID)
				.amountVatExcluded(Float.valueOf(IB1_INVOICE2_AMOUNT_VAT_EXCLUDED))
				.amountVatIncluded(Float.valueOf(IB1_INVOICE2_AMOUNT_VAT_INCLUDED))
				.currency(IB1_INVOICE2_CURRENCY)
				.dueDate(IB1_INVOICE2_DUE_DATE)
				.invoiceDate(IB1_INVOICE2_DATE)
				.invoiceDescription(IB1_INVOICE2_DESCRIPTION)
				.invoiceName(IB1_INVOICE2_NAME)
				.invoiceNumber(IB1_INVOICE2_NUMBER)
				.invoiceStatus(IB1_INVOICE2_STATUS)
				.invoiceType(IB1_INVOICE2_TYPE)
				.ocrNumber(IB1_INVOICE2_OCR_NUMBER)
				.organizationNumber(IB1_INVOICE2_ORGANIZATION_NUMBER)
				.reversedVat(IB1_INVOICE2_REVERSED_VAT)
				.rounding(Float.valueOf(IB1_INVOICE2_ROUNDING))
				.totalAmount(Float.valueOf(IB1_INVOICE2_TOTAL_AMOUNT))
				.vat(Float.valueOf(IB1_INVOICE2_VAT))
				.vatEligibleAmount(Float.valueOf(IB1_INVOICE2_VAT_ELIGIBLE_AMOUNT))

		) : List.of(new Invoice().facilityId(NO_MATCH));

	}
}
