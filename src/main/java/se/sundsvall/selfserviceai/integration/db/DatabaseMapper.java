package se.sundsvall.selfserviceai.integration.db;

import java.util.UUID;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;

public class DatabaseMapper {
	private DatabaseMapper() {}

	public static SessionEntity toSessionEntity(String municipalityId, final UUID sessionId, final String partyId) {
		return SessionEntity.builder()
			.withMunicipalityId(municipalityId)
			.withPartyId(partyId)
			.withSessionId(sessionId.toString())
			.build();
	}

	public static FileEntity toFileEntity(final UUID fileId) {
		return FileEntity.builder()
			.withFileId(fileId.toString())
			.build();
	}
}
