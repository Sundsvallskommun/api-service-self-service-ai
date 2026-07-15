package se.sundsvall.selfserviceai;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.agreement.Category;
import generated.se.sundsvall.eneo.CompletionModel;
import generated.se.sundsvall.eneo.FilePublic;
import generated.se.sundsvall.eneo.InfoBlobAskAssistantPublic;
import generated.se.sundsvall.eneo.InfoBlobMetadata;
import generated.se.sundsvall.eneo.InfoBlobPublicNoText;
import generated.se.sundsvall.eneo.Message;
import generated.se.sundsvall.eneo.SessionFeedback;
import generated.se.sundsvall.eneo.SessionPublic;
import generated.se.sundsvall.eneo.ToolAssistant;
import generated.se.sundsvall.eneo.UseTools;
import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseItemAddress;
import generated.se.sundsvall.installedbase.InstalledBaseItemMetaData;
import generated.se.sundsvall.invoices.CustomerInvoice;
import generated.se.sundsvall.invoices.InvoiceDetail;
import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementPoints;
import generated.se.sundsvall.measurementdata.MeasurementSerie;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Address;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Invoice;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.InvoiceRow;

public class TestFactory {
	private TestFactory() {}

	// Installedbase/Agreement/Measurement/Invoices test-object constants

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
	public static final String IB1_INVOICE1_CARE_OF = "invoiceCareOf1";
	public static final String IB1_INVOICE1_CITY = "invoiceCity1";
	public static final String IB1_INVOICE1_POSTAL_CODE = "invoicePostalCode1";
	public static final String IB1_INVOICE1_STREET = "invoiceStreet1";
	public static final LocalDate IB1_INVOICE1_DATE = LocalDate.of(2020, 2, 1);
	public static final LocalDate IB1_INVOICE1_DUE_DATE = LocalDate.of(2020, 2, 28);
	public static final String IB1_INVOICE2_AMOUNT_VAT_EXCLUDED = "2500.00";
	public static final String IB1_INVOICE2_AMOUNT_VAT_INCLUDED = "3000.00";
	public static final LocalDate IB1_INVOICE2_DUE_DATE = LocalDate.of(2024, 2, 13);
	public static final LocalDate IB1_INVOICE2_DATE = LocalDate.of(2024, 2, 28);
	public static final String IB1_INVOICE1_DESCRIPTION = "invoiceDescription1";
	public static final String IB1_INVOICE1_NAME = "invoiceName1";
	public static final String IB1_INVOICE1_NUMBER = "invoiceNumber1";
	public static final CustomerInvoice.InvoiceStatusEnum IB1_INVOICE1_STATUS = CustomerInvoice.InvoiceStatusEnum.SENT;
	public static final CustomerInvoice.InvoiceTypeEnum IB1_INVOICE1_TYPE = CustomerInvoice.InvoiceTypeEnum.INVOICE;
	public static final String IB1_INVOICE1_OCR_NUMBER = "invoiceOcrNumber1";
	public static final String IB1_INVOICE1_ORGANIZATION_NUMBER = "organizationNumber1";
	public static final Boolean IB1_INVOICE1_PDF_AVAILABLE = true;
	public static final String IB1_INVOICE1_ROUNDING = "-0.35";
	public static final String IB1_INVOICE1_TOTAL_AMOUNT = "1250.35";
	public static final String IB1_INVOICE1_VAT_ELIGIBLE_AMOUNT = "456.78";
	public static final String IB1_INVOICE2_DESCRIPTION = "invoiceDescription2";
	public static final String IB1_INVOICE2_NAME = "invoiceName2";
	public static final String IB1_INVOICE2_NUMBER = "invoiceNumber2";
	public static final CustomerInvoice.InvoiceStatusEnum IB1_INVOICE2_STATUS = CustomerInvoice.InvoiceStatusEnum.REMINDER;
	public static final CustomerInvoice.InvoiceTypeEnum IB1_INVOICE2_TYPE = CustomerInvoice.InvoiceTypeEnum.CONSOLIDATED_INVOICE;
	public static final String IB1_INVOICE2_OCR_NUMBER = "invoiceOcrNumber2";
	public static final String IB1_INVOICE2_ORGANIZATION_NUMBER = "organizationNumber2";
	public static final String IB1_INVOICE2_ROUNDING = "0.49";
	public static final String IB1_INVOICE2_TOTAL_AMOUNT = "2999.51";
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

	// Eneo test-object constants

	public static final UUID SESSION_ID = UUID.randomUUID();
	public static final OffsetDateTime SESSION_CREATED = OffsetDateTime.now().minusHours(12);
	public static final OffsetDateTime SESSION_UPDATED = OffsetDateTime.now().minusHours(11);
	public static final String SESSION_NAME = "sessionName";
	public static final UUID MESSAGE_ID = UUID.randomUUID();
	public static final OffsetDateTime MESSAGE_CREATED = OffsetDateTime.now().minusHours(10);
	public static final OffsetDateTime MESSAGE_UPDATED = OffsetDateTime.now().minusHours(9);
	public static final UUID FILE_ID = UUID.randomUUID();
	public static final OffsetDateTime FILE_CREATED = OffsetDateTime.now().minusHours(8);
	public static final OffsetDateTime FILE_UPDATED = OffsetDateTime.now().minusHours(7);
	public static final String FILE_MIME_TYPE = "mimeType";
	public static final String FILE_NAME = "name";
	public static final int FILE_SIZE = 123;
	public static final String ANSWER = "answer";
	public static final String QUESTION = "question";
	public static final String TEXT = "text";
	public static final SessionFeedback.ValueEnum VALUE = SessionFeedback.ValueEnum.NUMBER_1;
	public static final BigDecimal REFERENCE_SCORE = new BigDecimal("0.95");
	public static final UUID ASSISTANT_ID = UUID.randomUUID();
	public static final UUID REFERENCE_ID = UUID.randomUUID();
	public static final UUID GROUP_ID = UUID.randomUUID();
	public static final OffsetDateTime REFERENCE_CREATED = OffsetDateTime.now().minusHours(14);
	public static final OffsetDateTime REFERENCE_UPDATED = OffsetDateTime.now().minusHours(13);
	public static final UUID WEBSITE_ID = UUID.randomUUID();
	public static final UUID EMBEDDING_MODEL_ID = UUID.randomUUID();
	public static final int METADATA_SIZE = 789;
	public static final String METADATA_TITLE = "metadataTitle";
	public static final String METADATA_URL = "metadataUrl";
	public static final String BASE_URL = "baseUrl";
	public static final OffsetDateTime COMPLETION_MODEL_CREATED = OffsetDateTime.now().minusHours(14);
	public static final OffsetDateTime COMPLETION_MODEL_UPDATED = OffsetDateTime.now().minusHours(13);
	public static final UUID COMPLETION_MODEL_ID = UUID.randomUUID();
	public static final String DEPLOYMENT_NAME = "deploymentName";
	public static final String DESCRIPTION = "description";
	public static final String FAMILY = "family";
	public static final String HF_LINK = "hfLink";
	public static final String HOSTING = "hosting";
	public static final Boolean DEPRECATED = true;
	public static final boolean ORG_DEFAULT = true;
	public static final boolean ORG_ENABLED = true;
	public static final String NAME = "name";
	public static final String NICKNAME = "nickname";
	public static final Integer NR_BILLION_PARAMETERS = 33;
	public static final Boolean OPEN_SOURCE = true;
	public static final String ORG = "org";
	public static final boolean REASONING = true;
	public static final String STABILITY = "stability";
	public static final Integer TOKEN_LIMIT = 686;
	public static final boolean VISION = true;

	public static SessionPublic createSession() {
		return new SessionPublic()
			.createdAt(SESSION_CREATED)
			.feedback(createSessionFeedback())
			.id(SESSION_ID)
			.messages(List.of(new Message()
				.answer(ANSWER)
				.completionModel(createCompletionModel())
				.createdAt(MESSAGE_CREATED)
				.files(List.of(createFilePublic()))
				.id(MESSAGE_ID)
				.question(QUESTION)
				.references(List.of(createMessageReference()))
				.tools(createTools())
				.updatedAt(MESSAGE_UPDATED)))
			.name(SESSION_NAME)
			.updatedAt(SESSION_UPDATED);
	}

	private static UseTools createTools() {
		return new UseTools()
			.assistants(List.of(createAssistant()));
	}

	public static ToolAssistant createAssistant() {
		return new ToolAssistant().id(ASSISTANT_ID);
	}

	public static InfoBlobAskAssistantPublic createReference() {
		return new InfoBlobAskAssistantPublic()
			.createdAt(REFERENCE_CREATED)
			.groupId(GROUP_ID)
			.id(REFERENCE_ID)
			.metadata(createMetadata())
			.score(REFERENCE_SCORE)
			.updatedAt(REFERENCE_UPDATED)
			.websiteId(WEBSITE_ID);
	}

	public static InfoBlobPublicNoText createMessageReference() {
		return new InfoBlobPublicNoText()
			.createdAt(REFERENCE_CREATED)
			.groupId(GROUP_ID)
			.id(REFERENCE_ID)
			.metadata(createMetadata())
			.updatedAt(REFERENCE_UPDATED)
			.websiteId(WEBSITE_ID);
	}

	private static InfoBlobMetadata createMetadata() {
		return new InfoBlobMetadata()
			.embeddingModelId(EMBEDDING_MODEL_ID)
			.size(METADATA_SIZE)
			.title(METADATA_TITLE)
			.url(METADATA_URL);
	}

	private static CompletionModel createCompletionModel() {
		return new CompletionModel()
			.baseUrl(BASE_URL)
			.createdAt(COMPLETION_MODEL_CREATED)
			.deploymentName(DEPLOYMENT_NAME)
			.description(DESCRIPTION)
			.family(FAMILY)
			.hfLink(HF_LINK)
			.hosting(HOSTING)
			.id(COMPLETION_MODEL_ID)
			.isDeprecated(DEPRECATED)
			.isOrgDefault(ORG_DEFAULT)
			.isOrgEnabled(ORG_ENABLED)
			.name(NAME)
			.nickname(NICKNAME)
			.nrBillionParameters(NR_BILLION_PARAMETERS)
			.openSource(OPEN_SOURCE)
			.org(ORG)
			.reasoning(REASONING)
			.stability(STABILITY)
			.tokenLimit(TOKEN_LIMIT)
			.updatedAt(COMPLETION_MODEL_UPDATED)
			.vision(VISION);
	}

	public static SessionFeedback createSessionFeedback() {
		return new SessionFeedback()
			.text(TEXT)
			.value(VALUE);
	}

	public static FilePublic createFilePublic() {
		return new FilePublic()
			.createdAt(FILE_CREATED)
			.id(FILE_ID)
			.mimetype(FILE_MIME_TYPE)
			.name(FILE_NAME)
			.size(FILE_SIZE)
			.updatedAt(FILE_UPDATED);
	}

	public static InstalledBaseCustomer createCustomer() {
		return new InstalledBaseCustomer()
			.customerNumber(CUSTOMER_NUMBER)
			.items(List.of(
				createInstalledBaseItem(1),
				createInstalledBaseItem(2)))
			.partyId(PARTY_ID);
	}

	public static InstalledBaseItem createInstalledBaseItem(int baseItem) {
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

	public static Facility createFacility() {
		return createFacility(null);
	}

	public static Facility createFacility(final Consumer<Facility> consumer) {
		var facility = Facility.builder()
			.withFacilityId("facilityId1")
			.withAddress(Address.builder()
				.withStreet("street")
				.withPostalCode("postalCode")
				.withCareOf("careOf")
				.withCity("city")
				.build())
			.build();

		if (consumer != null) {
			consumer.accept(facility);
		}

		return facility;
	}

	public static se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Invoice createInvoice() {
		return createInvoice(null);
	}

	public static se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Invoice createInvoice(final Consumer<Invoice> consumer) {
		var invoice = se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Invoice.builder()
			.withAmountVatExcluded(BigDecimal.valueOf(Double.parseDouble(IB1_INVOICE1_AMOUNT_VAT_EXCLUDED)))
			.withAmountVatIncluded(BigDecimal.valueOf(Double.parseDouble(IB1_INVOICE1_AMOUNT_VAT_INCLUDED)))
			.withDescription(IB1_INVOICE1_DESCRIPTION)
			.withFacilityIds(List.of(IB1_FACILITY_ID))
			.withInvoiceAddress(se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Address.builder()
				.withCareOf(IB1_CARE_OF)
				.withCity(IB1_CITY)
				.withPostalCode(IB1_POSTAL_CODE)
				.withStreet(IB1_STREET)
				.build())
			.withInvoicingDate(IB1_INVOICE1_DATE)
			.withInvoiceName(IB1_INVOICE1_NAME)
			.withInvoiceNumber(IB1_INVOICE1_NUMBER)
			.withInvoiceType(IB1_TYPE)
			.withDueDate(IB1_INVOICE1_DUE_DATE)
			.withOcrNumber(IB1_INVOICE1_OCR_NUMBER)
			.withOrganizationNumber(IB1_INVOICE1_ORGANIZATION_NUMBER)
			.withPdfAvailable(IB1_INVOICE1_PDF_AVAILABLE)
			.withRounding(BigDecimal.valueOf(Double.parseDouble(IB1_INVOICE1_ROUNDING)))
			.withStatus(IB1_INVOICE1_STATUS.toString())
			.withTotalAmount(BigDecimal.valueOf(Double.parseDouble(IB1_INVOICE1_TOTAL_AMOUNT)))
			.withVatEligibleAmount(BigDecimal.valueOf(Double.parseDouble(IB1_INVOICE1_VAT_ELIGIBLE_AMOUNT)))
			.withInvoiceRows(List.of(createInvoiceRow()))
			.build();

		if (consumer != null) {
			consumer.accept(invoice);
		}

		return invoice;
	}

	public static InvoiceDetail createInvoiceDetail() {
		return new InvoiceDetail()
			.amount(BigDecimal.valueOf(12))
			.amountVatExcluded(BigDecimal.valueOf(12))
			.fromDate(LocalDate.now())
			.toDate(LocalDate.now())
			.vat(BigDecimal.valueOf(10))
			.vatRate(BigDecimal.valueOf(10))
			.quantity(BigDecimal.valueOf(0.08493151))
			.description("description")
			.productCode("productCode")
			.productName("productName")
			.unit("unit");
	}

	public static InvoiceRow createInvoiceRow() {
		return createInvoiceRow(null);
	}

	public static InvoiceRow createInvoiceRow(final Consumer<InvoiceRow> consumer) {
		var invoiceRow = InvoiceRow.builder()
			.withAmount(BigDecimal.ONE)
			.withAmountVatExcluded(BigDecimal.ONE)
			.withVat(BigDecimal.ONE)
			.withVatRate(BigDecimal.ONE)
			.withQuantity(BigDecimal.ONE)
			.withUnit("unit")
			.withUnitPrice(BigDecimal.ONE)
			.withPeriodFrom("periodFrom")
			.withPeriodTo("periodTo")
			.withProductCode("productCode")
			.withProductName("productName")
			.build();

		if (consumer != null) {
			consumer.accept(invoiceRow);
		}

		return invoiceRow;
	}

	public static CustomerInvoice createCustomerInvoice() {
		return new CustomerInvoice()
			.facilityIds(List.of(IB1_FACILITY_ID))
			.amountVatExcluded(new BigDecimal(IB1_INVOICE1_AMOUNT_VAT_EXCLUDED))
			.amountVatIncluded(new BigDecimal(IB1_INVOICE1_AMOUNT_VAT_INCLUDED))
			.dueDate(IB1_INVOICE1_DUE_DATE)
			.invoiceDate(IB1_INVOICE1_DATE)
			.careOf(IB1_INVOICE1_CARE_OF)
			.city(IB1_INVOICE1_CITY)
			.postCode(IB1_INVOICE1_POSTAL_CODE)
			.street(IB1_INVOICE1_STREET)
			.invoiceDescription(IB1_INVOICE1_DESCRIPTION)
			.invoiceName(IB1_INVOICE1_NAME)
			.invoiceNumber(IB1_INVOICE1_NUMBER)
			.invoiceStatus(IB1_INVOICE1_STATUS)
			.invoiceType(IB1_INVOICE1_TYPE)
			.ocrNumber(IB1_INVOICE1_OCR_NUMBER)
			.organizationNumber(IB1_INVOICE1_ORGANIZATION_NUMBER)
			.pdfAvailable(IB1_INVOICE1_PDF_AVAILABLE)
			.rounding(new BigDecimal(IB1_INVOICE1_ROUNDING))
			.totalAmount(new BigDecimal(IB1_INVOICE1_TOTAL_AMOUNT))
			.vatEligibleAmount(new BigDecimal(IB1_INVOICE1_VAT_ELIGIBLE_AMOUNT))
			.details(List.of(createInvoiceDetail(), createInvoiceDetail(), createInvoiceDetail()));
	}
}
