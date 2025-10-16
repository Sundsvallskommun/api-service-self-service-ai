package se.sundsvall.selfserviceai.integration.eneo.mapper;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import generated.se.sundsvall.installedbase.InstalledBaseItemAddress;
import generated.se.sundsvall.installedbase.InstalledBaseItemMetaData;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.InstalledBase;

@ExtendWith(MockitoExtension.class)
class EneoMapperTest {

	private final EneoMapper eneoMapper = new EneoMapper();

	@Mock
	private InstalledBaseCustomer installedBaseCustomerMock;

	@Mock
	private InstalledBaseItem installedBaseItemMock;

	@Mock
	private InstalledBaseItemAddress installedBaseItemAddressMock;

	@Mock
	private InstalledBaseItemMetaData installedBaseItemMetaDataMock;

	@AfterEach
	void verifyNoMoreMockIntegrations() {
		verifyNoMoreInteractions(
			installedBaseCustomerMock,
			installedBaseItemAddressMock,
			installedBaseItemMetaDataMock,
			installedBaseItemMock);
	}

	@Test
	void toAskAssistantWithoutFiles() {
		// Arrange
		final var input = "input";

		// Act
		final var result = eneoMapper.toAskAssistant(input);

		// Assert
		assertThat(result.files()).isEmpty();
		assertThat(result.question()).isEqualTo(input);
	}

	@Test
	void toAskAssistantWithFiles() {
		// Arrange
		final var input = "question";
		final var fileId1 = UUID.randomUUID().toString();
		final var fileId2 = UUID.randomUUID().toString();
		final var files = new ArrayList<>(List.of(fileId1, fileId2));
		files.add(null); // To verify that null values are filtered out in result

		// Act
		final var result = eneoMapper.toAskAssistant(input, files);

		// Assert
		assertThat(result.question()).isEqualTo(input);
		assertThat(result.files()).hasSize(2)
			.allSatisfy(filePublic -> assertThat(filePublic).hasAllNullFieldsOrPropertiesExcept("id"))
			.satisfiesExactlyInAnyOrder(
				filePublic -> assertThat(filePublic.id()).isEqualTo(UUID.fromString(fileId1)),
				filePublic -> assertThat(filePublic.id()).isEqualTo(UUID.fromString(fileId2)));
	}

	@Test
	void toInformationFile() {
		// Arrange
		final var data = "{\"key\":\"value\"}";

		// Act
		final var result = eneoMapper.toInformationFile(data);

		// Assert
		assertThat(result.getBytes()).isEqualTo(data.getBytes(Charset.defaultCharset()));
		assertThat(result.getContentType()).isEqualTo("application/json");
		assertThat(result.getInputStream()).isNotNull();
		assertThat(result.getName()).isEqualTo("selfservice-ai-data.json");
		assertThat(result.getOriginalFilename()).isEqualTo("selfservice-ai-data.json");
		assertThat(result.getResource()).isNotNull();
		assertThat(new String(result.getBytes(), Charset.defaultCharset())).isEqualTo(data);
		assertThat(result.getSize()).isEqualTo(data.getBytes(Charset.defaultCharset()).length);
	}

	@Test
	void toFacilitiesFromMap() {
		final var facilityId = "facilityId";
		final var input = new HashMap<>(Map.of("123456", installedBaseCustomerMock));
		input.put("123457", null); // To verify filtering of null values

		when(installedBaseCustomerMock.getItems()).thenReturn(List.of(installedBaseItemMock));
		when(installedBaseItemMock.getFacilityId()).thenReturn(facilityId);
		when(installedBaseItemMock.getAddress()).thenReturn(installedBaseItemAddressMock);

		final var list = eneoMapper.toFacilities(input);

		assertThat(list).hasSize(1).satisfiesExactly(f -> {
			assertThat(f.getFacilityId()).isEqualTo(facilityId);
		});

		verify(installedBaseItemMock).getFacilityId();
		verify(installedBaseItemMock).getAddress();
		verify(installedBaseItemAddressMock).getCareOf();
		verify(installedBaseItemAddressMock).getCity();
		verify(installedBaseItemAddressMock).getPostalCode();
		verify(installedBaseItemAddressMock).getStreet();
	}

	@Test
	void toFacilitiesFromNullMap() {
		// Act and assert
		assertThat(eneoMapper.toFacilities((Map<String, InstalledBaseCustomer>) null)).isEmpty();
	}

	@Test
	void toFacilitiesFromEntry() {
		final var facilityId = "facilityId";

		when(installedBaseCustomerMock.getItems()).thenReturn(List.of(installedBaseItemMock));
		when(installedBaseItemMock.getFacilityId()).thenReturn(facilityId);
		when(installedBaseItemMock.getAddress()).thenReturn(installedBaseItemAddressMock);

		final var list = eneoMapper.toFacilities(Map.entry("123456", installedBaseCustomerMock));

		assertThat(list).hasSize(1).satisfiesExactly(bean -> {
			assertThat(bean.getFacilityId()).isEqualTo(facilityId);
		});

		verify(installedBaseItemMock).getFacilityId();
		verify(installedBaseItemMock).getAddress();
		verify(installedBaseItemAddressMock).getCareOf();
		verify(installedBaseItemAddressMock).getCity();
		verify(installedBaseItemAddressMock).getPostalCode();
		verify(installedBaseItemAddressMock).getStreet();
	}

	@Test
	void toFacilitiesFromNullEntry() {
		// Act and assert
		assertThat(eneoMapper.toFacilities((Map.Entry<String, InstalledBaseCustomer>) null)).isEmpty();
	}

	@Test
	void toFacility() {
		final var facilityId = "facilityId";

		when(installedBaseItemMock.getFacilityId()).thenReturn(facilityId);
		when(installedBaseItemMock.getAddress()).thenReturn(installedBaseItemAddressMock);

		final var bean = eneoMapper.toFacility(installedBaseItemMock);

		assertThat(bean.getFacilityId()).isEqualTo(facilityId);

		verify(installedBaseItemMock).getFacilityId();
		verify(installedBaseItemMock).getAddress();
		verify(installedBaseItemAddressMock).getCareOf();
		verify(installedBaseItemAddressMock).getCity();
		verify(installedBaseItemAddressMock).getPostalCode();
		verify(installedBaseItemAddressMock).getStreet();
	}

	@Test
	void toFacilityFromNull() {
		// Act and assert
		assertThat(eneoMapper.toFacility(null)).isNull();
	}

	@Test
	void toAddress() {
		// Arrange
		final var careOf = "careOf";
		final var city = "city";
		final var postalCode = "postalCode";
		final var street = "street";

		when(installedBaseItemAddressMock.getCareOf()).thenReturn(careOf);
		when(installedBaseItemAddressMock.getCity()).thenReturn(city);
		when(installedBaseItemAddressMock.getPostalCode()).thenReturn(postalCode);
		when(installedBaseItemAddressMock.getStreet()).thenReturn(street);

		// Act
		final var bean = eneoMapper.toAddress(installedBaseItemAddressMock);

		// Assert and verify
		assertThat(bean).hasNoNullFieldsOrProperties();
		assertThat(bean.getCareOf()).isEqualTo(careOf);
		assertThat(bean.getCity()).isEqualTo(city);
		assertThat(bean.getPostalCode()).isEqualTo(postalCode);
		assertThat(bean.getStreet()).isEqualTo(street);

		verify(installedBaseItemAddressMock).getCareOf();
		verify(installedBaseItemAddressMock).getCity();
		verify(installedBaseItemAddressMock).getPostalCode();
		verify(installedBaseItemAddressMock).getStreet();
	}

	@Test
	void toAddressFromNull() {
		// Act and assert
		assertThat(eneoMapper.toAddress(null)).isNull();
	}

	@Test
	void toMetadatas() {
		// Arrange
		final var displayName = "displayName";
		final var key = "key";
		final var type = "type";
		final var value = "value";
		final var input = new ArrayList<>(List.of(installedBaseItemMetaDataMock));
		input.add(null); // To verify filtering of null values

		when(installedBaseItemMetaDataMock.getDisplayName()).thenReturn(displayName);
		when(installedBaseItemMetaDataMock.getKey()).thenReturn(key);
		when(installedBaseItemMetaDataMock.getType()).thenReturn(type);
		when(installedBaseItemMetaDataMock.getValue()).thenReturn(value);

		// Act
		final var list = eneoMapper.toMetadatas(input);

		// Assert and verify
		assertThat(list).hasSize(1).satisfiesExactly(bean -> {
			assertThat(bean).hasNoNullFieldsOrProperties();
			assertThat(bean.getDisplayName()).isEqualTo(displayName);
			assertThat(bean.getName()).isEqualTo(key);
			assertThat(bean.getType()).isEqualTo(type);
			assertThat(bean.getValue()).isEqualTo(value);
		});

		verify(installedBaseItemMetaDataMock).getDisplayName();
		verify(installedBaseItemMetaDataMock).getKey();
		verify(installedBaseItemMetaDataMock).getType();
		verify(installedBaseItemMetaDataMock).getValue();
		verifyNoMoreInteractions(installedBaseItemAddressMock);
	}

	@Test
	void addInstalledBasesWhenMatch() {
		// Arrange
		final var facilityId = "123";
		final var installedBase = InstalledBase.builder().build();
		final var facility = Facility.builder()
			.withFacilityId(facilityId)
			.withInstalledBases(new ArrayList<>(List.of(installedBase)))
			.build();

		final var input = new ArrayList<>(List.of(installedBaseItemMock));
		input.add(null); // To verify filtering of null values

		when(installedBaseItemMock.getFacilityId()).thenReturn(facilityId);
		when(installedBaseItemMock.getPlacementId()).thenReturn(null);

		// Act
		eneoMapper.addInstalledBases(facility, input);

		// Assert and verify
		assertThat(facility.getInstalledBases()).hasSize(2)
			.satisfiesOnlyOnce(installedBase1 -> assertThat(installedBase1).isNotEqualTo(installedBase)
				.hasAllNullFieldsOrPropertiesExcept("information")
				.hasFieldOrPropertyWithValue("information", emptyList()))
			.satisfiesOnlyOnce(installedBase1 -> assertThat(installedBase1).isEqualTo(installedBase)
				.hasAllNullFieldsOrProperties());

		verify(installedBaseItemMock).getFacilityCommitmentEndDate();
		verify(installedBaseItemMock).getFacilityCommitmentStartDate();
		verify(installedBaseItemMock).getFacilityId();
		verify(installedBaseItemMock).getLastModifiedDate();
		verify(installedBaseItemMock).getMetaData();
		verify(installedBaseItemMock).getPlacementId();
		verify(installedBaseItemMock).getType();
	}

	@Test
	void addInstalledBasesWhenNoMatch() {
		// Arrange
		final var facilityId = "123";
		final var installedBase = InstalledBase.builder().build();
		final var facility = Facility.builder()
			.withFacilityId(facilityId)
			.withInstalledBases(new ArrayList<>(List.of(installedBase)))
			.build();

		// Act
		eneoMapper.addInstalledBases(facility, List.of(installedBaseItemMock));

		// Assert and verify
		assertThat(facility.getInstalledBases()).hasSize(1)
			.allSatisfy(installedBase1 -> assertThat(installedBase1).hasAllNullFieldsOrProperties())
			.satisfiesOnlyOnce(installedBase1 -> assertThat(installedBase1).isEqualTo(installedBase));

		verify(installedBaseItemMock).getFacilityId();
	}

	@Test
	void addInstalledBasesForNull() {
		// Act and assert
		assertDoesNotThrow(() -> eneoMapper.addInstalledBases(null, null));
	}

	@Test
	void toInstalledBase() {
		// Arrange
		final var facilityCommitmentEndDate = LocalDate.now();
		final var facilityCommitmentStartDate = LocalDate.now().minusYears(1);
		final var lastModifiedDate = LocalDate.now().minusDays(1);
		final var metadatas = List.of(installedBaseItemMetaDataMock);
		final var placementId = 666;
		final var type = "type";

		when(installedBaseItemMock.getFacilityCommitmentEndDate()).thenReturn(facilityCommitmentEndDate);
		when(installedBaseItemMock.getFacilityCommitmentStartDate()).thenReturn(facilityCommitmentStartDate);
		when(installedBaseItemMock.getLastModifiedDate()).thenReturn(lastModifiedDate);
		when(installedBaseItemMock.getMetaData()).thenReturn(metadatas);
		when(installedBaseItemMock.getPlacementId()).thenReturn(placementId);
		when(installedBaseItemMock.getType()).thenReturn(type);

		// Act
		final var bean = eneoMapper.toInstalledBase(installedBaseItemMock);

		// Assert and verify
		assertThat(bean).hasNoNullFieldsOrProperties();
		assertThat(bean.getCommitmentEndDate()).isEqualTo(facilityCommitmentEndDate);
		assertThat(bean.getCommitmentStartDate()).isEqualTo(facilityCommitmentStartDate);
		assertThat(bean.getInformation()).hasSize(1);
		assertThat(bean.getLastModifiedDate()).isEqualTo(lastModifiedDate);
		assertThat(bean.getPlacementId()).isEqualByComparingTo(placementId);
		assertThat(bean.getType()).isEqualTo(type);

		verify(installedBaseItemMock).getFacilityCommitmentEndDate();
		verify(installedBaseItemMock).getFacilityCommitmentStartDate();
		verify(installedBaseItemMock).getLastModifiedDate();
		verify(installedBaseItemMock).getMetaData();
		verify(installedBaseItemMock).getPlacementId();
		verify(installedBaseItemMock).getType();
		verify(installedBaseItemMetaDataMock).getDisplayName();
		verify(installedBaseItemMetaDataMock).getKey();
		verify(installedBaseItemMetaDataMock).getType();
		verify(installedBaseItemMetaDataMock).getValue();
	}

	@Test
	void toInstalledBaseFromNull() {
		// Act and assert
		assertThat(eneoMapper.toInstalledBase(null)).isNull();

	}

	@Test
	void toMetadatasFromNull() {
		// Act and assert
		assertThat(eneoMapper.toMetadatas(null)).isEmpty();
	}

	@Test
	void toMetadata() {
		// Arrange
		final var displayName = "displayName";
		final var key = "key";
		final var type = "type";
		final var value = "value";

		when(installedBaseItemMetaDataMock.getDisplayName()).thenReturn(displayName);
		when(installedBaseItemMetaDataMock.getKey()).thenReturn(key);
		when(installedBaseItemMetaDataMock.getType()).thenReturn(type);
		when(installedBaseItemMetaDataMock.getValue()).thenReturn(value);

		// Act
		final var bean = eneoMapper.toMetadata(installedBaseItemMetaDataMock);

		// Assert and verify
		assertThat(bean).hasNoNullFieldsOrProperties();
		assertThat(bean.getDisplayName()).isEqualTo(displayName);
		assertThat(bean.getName()).isEqualTo(key);
		assertThat(bean.getType()).isEqualTo(type);
		assertThat(bean.getValue()).isEqualTo(value);

		verify(installedBaseItemMetaDataMock).getDisplayName();
		verify(installedBaseItemMetaDataMock).getKey();
		verify(installedBaseItemMetaDataMock).getType();
		verify(installedBaseItemMetaDataMock).getValue();
	}

	@Test
	void toMetadataFromNull() {
		// Act and assert
		assertThat(eneoMapper.toMetadata(null)).isNull();
	}

	@Test
	void toIntricModel() {
		// Arrange
		final var facilityId = "facilityId";
		final var type1 = "type1";
		final var type2 = "type2";

		when(installedBaseCustomerMock.getItems()).thenReturn(List.of(installedBaseItemMock, installedBaseItemMock));
		when(installedBaseItemMock.getFacilityId()).thenReturn(facilityId);
		when(installedBaseItemMock.getType()).thenReturn(type1, type2);

		// Act
		final var result = eneoMapper.toIntricModel(Map.of("123456", installedBaseCustomerMock));

		// Assert and verify
		assertThat(result.getFacilities()).hasSize(1);
		assertThat(result.getFacilities().getFirst().getInstalledBases()).hasSize(2)
			.satisfiesExactlyInAnyOrder(
				installedBase -> assertThat(installedBase.getType()).isEqualTo(type1),
				installedBase -> assertThat(installedBase.getType()).isEqualTo(type2));

		verify(installedBaseCustomerMock).getCustomerNumber();
		verify(installedBaseCustomerMock).getPartyId();
		verify(installedBaseCustomerMock, times(2)).getItems();
		verify(installedBaseItemMock, times(2)).getAddress();
		verify(installedBaseItemMock, times(4)).getFacilityId();
		verify(installedBaseItemMock, times(2)).getFacilityCommitmentEndDate();
		verify(installedBaseItemMock, times(2)).getFacilityCommitmentStartDate();
		verify(installedBaseItemMock, times(2)).getLastModifiedDate();
		verify(installedBaseItemMock, times(2)).getMetaData();
		verify(installedBaseItemMock, times(2)).getPlacementId();
		verify(installedBaseItemMock, times(2)).getType();
	}

	@Test
	void toIntricModelFromNull() {
		assertThat(eneoMapper.toIntricModel(null)).isNull();
	}

	@Test
	void toIntricModelFromEmptyMap() {
		assertThat(eneoMapper.toIntricModel(emptyMap())).isNull();
	}

	@Test
	void toIntricModelFromEmptyObject() {
		final var result = eneoMapper.toIntricModel(Map.of("123456", installedBaseCustomerMock));

		assertThat(result)
			.hasAllNullFieldsOrPropertiesExcept("facilities")
			.hasFieldOrPropertyWithValue("facilities", emptyList());

		verify(installedBaseCustomerMock).getCustomerNumber();
		verify(installedBaseCustomerMock).getPartyId();
		verify(installedBaseCustomerMock, times(2)).getItems();
	}
}
