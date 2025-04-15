package se.sundsvall.selfserviceai.integration.db.mapper;

import static java.util.Optional.ofNullable;

import java.util.UUID;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;
import se.sundsvall.selfserviceai.integration.db.model.HistoryEntity;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;

public class DatabaseMapper {
	private DatabaseMapper() {}

	public static SessionEntity toSessionEntity(String municipalityId, final UUID sessionId, final String partyId) {
		return SessionEntity.builder()
			.withMunicipalityId(municipalityId)
			.withPartyId(partyId)
			.withSessionId(ofNullable(sessionId).map(UUID::toString).orElse(null))
			.build();
	}

	public static FileEntity toFileEntity(final UUID fileId) {
		return FileEntity.builder()
			.withFileId(ofNullable(fileId).map(UUID::toString).orElse(null))
			.build();
	}

	public static HistoryEntity toHistoryEntity(final String customerNbr, final String limeHistory, final String partyId, final UUID sessionId) {
		return HistoryEntity.builder()
			.withCustomerNbr(customerNbr)
			.withLimeHistory(limeHistory)
			.withPartyId(partyId)
			.withSessionId(ofNullable(sessionId).map(UUID::toString).orElse(null))
			.build();
	}
}
