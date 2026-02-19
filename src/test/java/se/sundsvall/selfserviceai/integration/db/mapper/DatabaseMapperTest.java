package se.sundsvall.selfserviceai.integration.db.mapper;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class DatabaseMapperTest {

	@Test
	void toSessionEntity() {
		final var municipalityId = "municipalityId";
		final var sessionId = UUID.randomUUID();
		final var partyId = "partyId";

		final var result = DatabaseMapper.toSessionEntity(municipalityId, sessionId, partyId);

		assertThat(result).hasAllNullFieldsOrPropertiesExcept("files", "municipalityId", "sessionId", "partyId");
		assertThat(result.getFiles()).isEmpty();
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getSessionId()).isEqualTo(sessionId.toString());
		assertThat(result.getPartyId()).isEqualTo(partyId);
	}

	@Test
	void toSessionEntityFromNull() {
		assertThat(DatabaseMapper.toSessionEntity(null, null, null))
			.hasAllNullFieldsOrPropertiesExcept("files")
			.hasFieldOrPropertyWithValue("files", emptyList());
	}

	@Test
	void toFileEntity() {
		final var fileId = UUID.randomUUID();

		assertThat(DatabaseMapper.toFileEntity(fileId))
			.hasNoNullFieldsOrProperties()
			.hasFieldOrPropertyWithValue("fileId", fileId.toString());
	}

	@Test
	void toFileEntityFromNull() {
		assertThat(DatabaseMapper.toFileEntity(null)).hasAllNullFieldsOrProperties();
	}

	@Test
	void toHistoryEntity() {
		final var customerNbr = "customerNbr";
		final var limeHistory = "limeHistory";
		final var partyId = "partyId";
		final var sessionId = UUID.randomUUID();

		final var result = DatabaseMapper.toHistoryEntity(customerNbr, limeHistory, partyId, sessionId);

		assertThat(result).hasNoNullFieldsOrPropertiesExcept("created");
		assertThat(result.getCustomerNbr()).isEqualTo(customerNbr);
		assertThat(result.getLimeHistory()).isEqualTo(limeHistory);
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getSessionId()).isEqualTo(sessionId.toString());
	}

	@Test
	void toHistoryEntityFromNull() {
		assertThat(DatabaseMapper.toHistoryEntity(null, null, null, null)).hasAllNullFieldsOrProperties();
	}
}
