package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseItemAddress;
import generated.se.sundsvall.installedbase.InstalledBaseItemMetaData;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.collections4.MapUtils;
import se.sundsvall.selfserviceai.integration.intric.model.AskAssistant;
import se.sundsvall.selfserviceai.integration.intric.model.FilePublic;
import se.sundsvall.selfserviceai.integration.intric.model.InformationFile;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.IntricModel;

public class IntricMapper {
	private IntricMapper() {}

	/**
	 * Maps incoming question string into an AskAssistant request
	 *
	 * @param  input question to be asked
	 * @return       AskAssistant object with provided data
	 */
	public static AskAssistant toAskAssistant(String input) {
		return toAskAssistant(input, emptyList());
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
			.withFiles(toFilesPublic(fileReferences))
			.build();
	}

	private static List<FilePublic> toFilesPublic(List<String> fileReferences) {
		return ofNullable(fileReferences).orElse(emptyList()).stream()
			.map(IntricMapper::toFilePublic)
			.filter(Objects::nonNull)
			.toList();
	}

	private static FilePublic toFilePublic(String fileId) {
		return ofNullable(fileId)
			.map(UUID::fromString)
			.map(id -> FilePublic.builder()
				.withId(id)
				.build())
			.orElse(null);
	}

	/**
	 * Maps incoming data to a InformationFile object implementing the MultipartFile interface
	 *
	 * @param  data string of json data that is to be the content of the file
	 * @return      a InformationFile object representing the provided data
	 */
	public static InformationFile toInformationFile(String data) {
		return InformationFile
			.create()
			.withData(data);
	}

	/**
	 * Method for mapping installed base customer object into a intric information model
	 *
	 * @param  installedBaseCustomers map containing all requested installedbases for the customer, where key is
	 *                                customerEngagementOrgId and value is response from InstalledBase service
	 * @return                        A InstalledBase object containing provided installed base customer data
	 */
	public static IntricModel toIntricModel(final Map<String, InstalledBaseCustomer> installedBaseCustomers) {
		final var intricModel = ofNullable(installedBaseCustomers)
			.filter(MapUtils::isNotEmpty)
			.map(ibc -> IntricModel.builder()
				.withCustomerNumber(ibc.values().iterator().next().getCustomerNumber()) // Use first entry to retrive customer nr and party id (as its always the same for all ib:s)
				.withPartyId(ibc.values().iterator().next().getPartyId())
				.withFacilities(toFacilities(ibc))
				.build())
			.orElse(null);

		// Attach matching installed bases to each facility
		if (nonNull(intricModel)) {
			final var installedBases = ofNullable(installedBaseCustomers).orElse(emptyMap()).values().stream()
				.map(InstalledBaseCustomer::getItems)
				.flatMap(List::stream)
				.toList();

			intricModel.getFacilities().forEach(facility -> addCommitments(facility, installedBases));
		}

		return intricModel;
	}

	private static List<Facility> toFacilities(Map<String, InstalledBaseCustomer> installedBases) {
		return installedBases.entrySet().stream()
			.map(IntricMapper::toFacilities)
			.flatMap(List::stream)
			.filter(distinctByValue(Facility::getFacilityId))
			.toList();
	}

	private static List<Facility> toFacilities(Entry<String, InstalledBaseCustomer> installedBaseEntry) {
		return ofNullable(installedBaseEntry.getValue().getItems()).orElse(emptyList()).stream()
			.map(IntricMapper::toFacility)
			.toList();
	}

	private static Facility toFacility(InstalledBaseItem item) {
		return Facility.builder()
			.withAddress(toAddress(item.getAddress()))
			.withFacilityId(item.getFacilityId())
			.build();
	}

	private static <T> Predicate<T> distinctByValue(Function<? super T, ?> keyExtractor) {
		final Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	private static void addCommitments(Facility facility, List<InstalledBaseItem> installedBaseItems) {
		facility.getInstalledBases().addAll(
			ofNullable(installedBaseItems).orElse(emptyList()).stream()
				.filter(ib -> Objects.equals(facility.getFacilityId(), ib.getFacilityId()))
				.map(IntricMapper::toCommitment)
				.filter(Objects::nonNull)
				.toList());
	}

	private static InstalledBase toCommitment(InstalledBaseItem installedBaseItem) {
		return ofNullable(installedBaseItem)
			.map(item -> InstalledBase.builder().withType(item.getType())
				.withPlacementId(item.getPlacementId())
				.withCommitmentStartDate(item.getFacilityCommitmentStartDate())
				.withCommitmentEndDate(item.getFacilityCommitmentEndDate())
				.withLastModifiedDate(item.getLastModifiedDate())
				.withInformation(toInformation(item.getMetaData()))
				.build())
			.orElse(null);
	}

	private static List<InstalledBase.Metadata> toInformation(List<InstalledBaseItemMetaData> metadatas) {
		return ofNullable(metadatas).orElse(emptyList()).stream()
			.map(IntricMapper::toMetadata)
			.toList();
	}

	private static InstalledBase.Metadata toMetadata(InstalledBaseItemMetaData metadata) {
		return InstalledBase.Metadata.builder()
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
}
