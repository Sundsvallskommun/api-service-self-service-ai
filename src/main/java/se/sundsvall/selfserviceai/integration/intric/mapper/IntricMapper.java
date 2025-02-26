package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseItemAddress;
import generated.se.sundsvall.installedbase.InstalledBaseItemMetaData;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import se.sundsvall.selfserviceai.integration.intric.model.AskAssistant;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;

public class IntricMapper {
	private IntricMapper() {}

	/**
	 * Maps incoming question string into an AskAssistant request
	 *
	 * @param  input question to be asked
	 * @return       AskAssistant object with provided data
	 */
	public static AskAssistant toAskAssistant(String input) {
		return toAskAssistant(input, null);
	}

	/**
	 * Maps incoming question string into an AskAssistant request
	 *
	 * @param  input          question to be asked
	 * @param  fileReferences id of previously stored files that shall be used to answer question
	 * @return                AskAssistant object with provided data
	 */
	public static AskAssistant toAskAssistant(String input, List<String> fileReferences) {
		return AskAssistant.builder()
			.withQuestion(input)
			.withFiles(fileReferences)
			.build();
	}

	/**
	 * Method for mapping installed base customer object into top level of intric filecontent model
	 *
	 * @param  customer InstalledBaseCustomer from installed base service with information to convert into top level of the
	 *                  filecontent model
	 * @return          A InstalledBase object containing provided installed base customer data
	 */
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

	private static se.sundsvall.selfserviceai.integration.intric.model.filecontent.Address toAddress(InstalledBaseItemAddress address) {
		return ofNullable(address)
			.map(a -> se.sundsvall.selfserviceai.integration.intric.model.filecontent.Address.builder()
				.withCareOf(a.getCareOf())
				.withCity(a.getCity())
				.withPostalCode(a.getPostalCode())
				.withStreet(a.getStreet())
				.build())
			.orElse(null);
	}

	private static LocalDate toLocalDate(Date date) {
		return ofNullable(date)
			.map(d -> date.toInstant().atZone(systemDefault()).toLocalDate())
			.orElse(null);
	}
}
