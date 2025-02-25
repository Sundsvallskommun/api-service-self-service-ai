package se.sundsvall.selfserviceai.service;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseItemAddress;
import generated.se.sundsvall.installedbase.InstalledBaseItemMetaData;
import generated.se.sundsvall.invoices.Address;
import generated.se.sundsvall.invoices.Invoice;
import generated.se.sundsvall.measurementdata.Data;
import generated.se.sundsvall.measurementdata.MeasurementPoints;
import generated.se.sundsvall.measurementdata.MeasurementSerie;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import se.sundsvall.selfserviceai.service.model.Facility;
import se.sundsvall.selfserviceai.service.model.InstalledBase;
import se.sundsvall.selfserviceai.service.model.MeasurementData;

@Component
public class IntricMapper {
	private IntricMapper() {}

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
		.findAndRegisterModules()
		.setDateFormat(new SimpleDateFormat(ISO_8601_EXTENDED_DATE_FORMAT.getPattern()))
		.setSerializationInclusion(NON_NULL);

	public static InstalledBase toInstalledBase(InstalledBaseCustomer customer) {
		return ofNullable(customer)
			.map(c -> InstalledBase.builder()
				.withCustomerNumber(c.getCustomerNumber())
				.withPartyId(c.getPartyId())
				.withFacilities(toFacilities(c.getItems()))
				.build())
			.orElse(null);
	}

	private static List<Facility> toFacilities(List<InstalledBaseItem> installedBaseItems) {
		return ofNullable(installedBaseItems).orElse(emptyList())
			.stream()
			.map(IntricMapper::toFacility)
			.toList();
	}

	private static Facility toFacility(InstalledBaseItem item) {
		return Facility.builder()
			.withAddress(toAddress(item.getAddress()))
			.withType(item.getType())
			.withFacilityId(item.getFacilityId())
			.withPlacementId(item.getPlacementId())
			.withCommitmentStartDate(toLocalDate(item.getFacilityCommitmentStartDate()))
			.withCommitmentEndDate(toLocalDate(item.getFacilityCommitmentEndDate()))
			.withLastModifiedDate(toLocalDate(item.getLastModifiedDate()))
			.withInformation(toInformation(item.getMetaData()))
			.build();
	}

	private static List<Facility.Metadata> toInformation(List<InstalledBaseItemMetaData> metadatas) {
		return ofNullable(metadatas).orElse(emptyList())
			.stream()
			.map(IntricMapper::toMetadata)
			.toList();
	}

	private static Facility.Metadata toMetadata(InstalledBaseItemMetaData metadata) {
		return Facility.Metadata.builder()
			.withDisplayName(metadata.getDisplayName())
			.withName(metadata.getKey())
			.withType(metadata.getType())
			.withValue(metadata.getValue())
			.build();
	}

	private static se.sundsvall.selfserviceai.service.model.Address toAddress(InstalledBaseItemAddress address) {
		return ofNullable(address)
			.map(a -> se.sundsvall.selfserviceai.service.model.Address.builder()
				.withCareOf(a.getCareOf())
				.withCity(a.getCity())
				.withPostalCode(a.getPostalCode())
				.withStreet(a.getStreet())
				.build())
			.orElse(null);
	}

	public static InstalledBase addAgreements(InstalledBase installedBase, List<Agreement> agreements) {
		ofNullable(agreements).orElse(emptyList())
			.stream()
			.forEach(agreement -> attachToFacility(installedBase, agreement));

		return installedBase;
	}

	private static void attachToFacility(InstalledBase installedBase, Agreement agreement) {
		if (Objects.nonNull(agreement)) {
			ofNullable(installedBase.getFacilities()).orElse(emptyList())
				.stream()
				.filter(f -> f.getFacilityId().equals(agreement.getFacilityId()))
				.findFirst()
				.ifPresent(f -> f.getAgreements().add(toAgreement(agreement)));
		}
	}

	private static se.sundsvall.selfserviceai.service.model.Agreement toAgreement(Agreement agreement) {
		return se.sundsvall.selfserviceai.service.model.Agreement.builder()
			.withAgreementId(agreement.getAgreementId())
			.withBound(ofNullable(agreement.getBinding()).map(Boolean::booleanValue).orElse(false))
			.withBindingRule(agreement.getBindingRule())
			.withCategory(agreement.getCategory().name())
			.withDescription(agreement.getDescription())
			.withFromDate(toLocalDate(agreement.getFromDate()))
			.build();
	}

	public static InstalledBase addMeasurements(InstalledBase installedBase, List<Data> measurementDatas) {
		ofNullable(measurementDatas).orElse(emptyList())
			.stream()
			.forEach(data -> attachToFacility(installedBase, data));

		return installedBase;
	}

	private static void attachToFacility(InstalledBase installedBase, Data data) {
		if (Objects.nonNull(data)) {
			ofNullable(installedBase.getFacilities()).orElse(emptyList())
				.stream()
				.filter(f -> f.getFacilityId().equals(data.getFacilityId()))
				.findFirst()
				.ifPresent(f -> f.getMeasurements().addAll(toMeasurementDatas(data)));
		}
	}

	private static List<MeasurementData> toMeasurementDatas(Data data) {
		return ofNullable(data.getMeasurementSeries()).orElse(emptyList())
			.stream()
			.map(serie -> toMeasurementData(data, serie))
			.flatMap(List::stream)
			.toList();
	}

	private static List<MeasurementData> toMeasurementData(Data data, MeasurementSerie serie) {
		return ofNullable(serie.getMeasurementPoints()).orElse(emptyList())
			.stream()
			.map(point -> toMeasurementData(data, serie, point))
			.toList();
	}

	private static MeasurementData toMeasurementData(Data data, MeasurementSerie serie, MeasurementPoints measurementPoint) {
		return MeasurementData.builder()
			.withCategory(data.getCategory().name())
			.withMeasurementType(serie.getMeasurementType())
			.withTimestamp(toOffsetDateTime(measurementPoint.getTimestamp()))
			.withUnit(serie.getUnit())
			.withValue(measurementPoint.getValue().setScale(2, RoundingMode.HALF_DOWN))
			.build();
	}

	public static InstalledBase addInvoices(InstalledBase installedBase, List<Invoice> invoices) {
		ofNullable(invoices).orElse(emptyList())
			.stream()
			.forEach(invoice -> attachToFacility(installedBase, invoice));

		return installedBase;
	}

	private static void attachToFacility(InstalledBase installedBase, Invoice invoice) {
		if (Objects.nonNull(invoice)) {
			ofNullable(installedBase.getFacilities()).orElse(emptyList())
				.stream()
				.filter(f -> f.getFacilityId().equals(invoice.getFacilityId()))
				.findFirst()
				.ifPresent(f -> f.getInvoices().add(toInvoice(invoice)));
		}
	}

	private static se.sundsvall.selfserviceai.service.model.Invoice toInvoice(Invoice invoice) {
		return se.sundsvall.selfserviceai.service.model.Invoice.builder()
			.withAmountVatExcluded(toBigDecimal(invoice.getAmountVatExcluded()))
			.withAmountVatIncluded(toBigDecimal(invoice.getAmountVatIncluded()))
			.withCurrency(invoice.getCurrency())
			.withDescription(invoice.getInvoiceDescription())
			.withDueDate(toLocalDate(invoice.getDueDate()))
			.withInvoiceAddress(toAddress(invoice.getInvoiceAddress()))
			.withInvoiceName(invoice.getInvoiceName())
			.withInvoiceNumber(invoice.getInvoiceNumber())
			.withInvoiceType(invoice.getInvoiceType().name())
			.withInvoicingDate(toLocalDate(invoice.getInvoiceDate()))
			.withOcrNumber(invoice.getOcrNumber())
			.withOrganizationNumber(invoice.getOrganizationNumber())
			.withPdfAvailable(toBoolean(invoice.getPdfAvailable()))
			.withReversedVat(toBoolean(invoice.getReversedVat()))
			.withRounding(toBigDecimal(invoice.getRounding()))
			.withStatus(invoice.getInvoiceStatus().name())
			.withTotalAmount(toBigDecimal(invoice.getTotalAmount()))
			.withVat(toBigDecimal(invoice.getVat()))
			.withVatEligibleAmount(toBigDecimal(invoice.getVatEligibleAmount()))
			.build();
	}

	private static se.sundsvall.selfserviceai.service.model.Address toAddress(Address address) {
		return ofNullable(address)
			.map(a -> se.sundsvall.selfserviceai.service.model.Address.builder()
				.withCareOf(a.getCareOf())
				.withCity(a.getCity())
				.withPostalCode(a.getPostcode())
				.withStreet(a.getStreet())
				.build())
			.orElse(null);
	}

	private static LocalDate toLocalDate(Date date) {
		return ofNullable(date)
			.map(d -> date.toInstant().atZone(systemDefault()).toLocalDate())
			.orElse(null);
	}

	private static BigDecimal toBigDecimal(Float value) {
		return ofNullable(value)
			.map(BigDecimal::valueOf)
			.map(bd -> bd.setScale(2, RoundingMode.HALF_DOWN))
			.orElse(null);
	}

	private static boolean toBoolean(Boolean value) {
		return ofNullable(value)
			.map(Boolean::booleanValue)
			.orElse(false);
	}

	private static OffsetDateTime toOffsetDateTime(Date date) {
		return ofNullable(date)
			.map(Date::toInstant)
			.map(d -> d.atOffset(OffsetDateTime.now(systemDefault()).getOffset()))
			.orElse(null);
	}

	public static String toJsonString(InstalledBase installedBase) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(installedBase);
	}
}
