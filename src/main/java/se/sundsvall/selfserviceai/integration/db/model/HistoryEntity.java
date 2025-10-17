package se.sundsvall.selfserviceai.integration.db.model;

import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Length;
import org.hibernate.annotations.TimeZoneStorage;

@Entity
@Table(name = "history", indexes = {
	@Index(name = "session_id_idx", columnList = "session_id"),
	@Index(name = "party_id_idx", columnList = "party_id")
})
@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoryEntity {

	@Id // No automatic generated value as this should be set to same uuid as the one from the Eneo create session response
	private String sessionId;

	private String partyId;

	private String customerNbr;

	@Column(name = "lime_history", length = Length.LONG32)
	private String limeHistory;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@PrePersist
	void prePersist() {
		this.created = OffsetDateTime.now();
	}
}
