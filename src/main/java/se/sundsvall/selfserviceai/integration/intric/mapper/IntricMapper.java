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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import se.sundsvall.selfserviceai.integration.intric.model.AskAssistant;
import se.sundsvall.selfserviceai.integration.intric.model.FilePublic;
import se.sundsvall.selfserviceai.integration.intric.model.InformationFile;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.IntricModel;

@Component
public class IntricMapper {

	private static <T> Predicate<T> distinctByValue(final Function<? super T, ?> keyExtractor) {
		final Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	/**
	 * Maps incoming question string into an AskAssistant request
	 *
	 * @param  input question to be asked
	 * @return       AskAssistant object with provided data
	 */
	public AskAssistant toAskAssistant(final String input) {
		return toAskAssistant(input, emptyList());
	}

	/**
	 * Maps incoming question string into an AskAssistant request
	 *
	 * @param  input          question to be asked
	 * @param  fileReferences id of previously stored files that shall be used to answer question
	 * @return                AskAssistant object with provided data
	 */
	public AskAssistant toAskAssistant(final String input, List<String> fileReferences) {
		return AskAssistant.builder()
			.withQuestion(input)
			.withFiles(toFilesPublic(fileReferences))
			.build();
	}

	static List<FilePublic> toFilesPublic(final List<String> fileReferences) {
		return ofNullable(fileReferences).orElse(emptyList()).stream()
			.map(IntricMapper::toFilePublic)
			.filter(Objects::nonNull)
			.toList();
	}

	static FilePublic toFilePublic(final String fileId) {
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
	public InformationFile toInformationFile(final String data) {
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
	public IntricModel toIntricModel(final Map<String, InstalledBaseCustomer> installedBaseCustomers) {
		final var intricModel = ofNullable(installedBaseCustomers)
			.filter(MapUtils::isNotEmpty)
			.map(ibc -> IntricModel.builder()
				.withCustomerNumber(ibc.values().iterator().next().getCustomerNumber()) // Use first entry to retrieve customer nr and party id (as its always the same for all ib:s)
				.withPartyId(ibc.values().iterator().next().getPartyId())
				.withFacilities(toFacilities(ibc))
				.build())
			.orElse(null);

		// Attach matching installed bases to each facility
		if (nonNull(intricModel)) {
			final var installedBases = Optional.of(installedBaseCustomers).orElse(emptyMap()).values().stream()
				.map(InstalledBaseCustomer::getItems)
				.flatMap(List::stream)
				.toList();

			intricModel.getFacilities().forEach(facility -> addInstalledBases(facility, installedBases));
		}

		return intricModel;
	}

	List<Facility> toFacilities(final Map<String, InstalledBaseCustomer> installedBases) {
		return ofNullable(installedBases).orElse(emptyMap()).entrySet().stream()
			.map(this::toFacilities)
			.flatMap(List::stream)
			.filter(distinctByValue(Facility::getFacilityId))
			.toList();
	}

	List<Facility> toFacilities(final Entry<String, InstalledBaseCustomer> installedBaseEntry) {
		return ofNullable(installedBaseEntry)
			.map(Entry::getValue)
			.map(InstalledBaseCustomer::getItems)
			.orElse(emptyList())
			.stream()
			.map(this::toFacility)
			.filter(Objects::nonNull)
			.toList();
	}

	Facility toFacility(final InstalledBaseItem item) {
		return ofNullable(item)
			.map(i -> Facility.builder()
				.withAddress(toAddress(i.getAddress()))
				.withFacilityId(i.getFacilityId())
				.build())
			.orElse(null);
	}

	void addInstalledBases(final Facility facility, final List<InstalledBaseItem> installedBaseItems) {
		ofNullable(facility).ifPresent(f -> f.getInstalledBases().addAll(
			ofNullable(installedBaseItems).orElse(emptyList()).stream()
				.filter(Objects::nonNull)
				.filter(ib -> Objects.equals(facility.getFacilityId(), ib.getFacilityId()))
				.map(this::toInstalledBase)
				.toList()));
	}

	InstalledBase toInstalledBase(final InstalledBaseItem installedBaseItem) {
		return ofNullable(installedBaseItem)
			.map(item -> InstalledBase.builder()
				.withType(item.getType())
				.withPlacementId(item.getPlacementId())
				.withCommitmentStartDate(item.getFacilityCommitmentStartDate())
				.withCommitmentEndDate(item.getFacilityCommitmentEndDate())
				.withLastModifiedDate(item.getLastModifiedDate())
				.withInformation(toMetadatas(item.getMetaData()))
				.build())
			.orElse(null);
	}

	List<InstalledBase.Metadata> toMetadatas(final List<InstalledBaseItemMetaData> metadatas) {
		return ofNullable(metadatas).orElse(emptyList()).stream()
			.map(this::toMetadata)
			.filter(Objects::nonNull)
			.toList();
	}

	InstalledBase.Metadata toMetadata(final InstalledBaseItemMetaData metadata) {
		return ofNullable(metadata)
			.map(m -> InstalledBase.Metadata.builder()
				.withDisplayName(metadata.getDisplayName())
				.withName(metadata.getKey())
				.withType(metadata.getType())
				.withValue(metadata.getValue())
				.build())
			.orElse(null);
	}

	se.sundsvall.selfserviceai.integration.intric.model.filecontent.Address toAddress(final InstalledBaseItemAddress address) {
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
