package se.sundsvall.selfserviceai.integration.db.model;

import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TimeZoneStorage;

@Entity
@Table(name = "session", indexes = {
	@Index(name = "municipality_id_idx", columnList = "municipality_id"),
	@Index(name = "session_id_municipality_id_idx", columnList = "session_id, municipality_id")
})
@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionEntity {

	@Id // No automatic generated value as this should be set to uuid from the Eneo create session response
	private String sessionId;

	@Column(name = "municipality_id", nullable = false)
	private String municipalityId;

	@Column(name = "party_id", nullable = false)
	private String partyId;

	@Column(name = "customer_nbr")
	private String customerNbr;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "initialized")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime initialized;

	@Column(name = "last_accessed")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime lastAccessed;

	@Column(name = "status", columnDefinition = "mediumtext")
	private String status;

	@Builder.Default
	@OneToMany
	@JoinColumn(name = "session_id", referencedColumnName = "sessionId", foreignKey = @ForeignKey(name = "fk_session_file"))
	private List<FileEntity> files = new ArrayList<>();

	@PrePersist
	void prePersist() {
		this.created = OffsetDateTime.now();
	}
}
