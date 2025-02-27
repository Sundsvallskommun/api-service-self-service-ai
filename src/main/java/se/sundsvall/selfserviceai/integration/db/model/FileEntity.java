package se.sundsvall.selfserviceai.integration.db.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file", indexes = {
	@Index(name = "file_id_idx", columnList = "file_id"),
	@Index(name = "session_id_idx", columnList = "session_id")

}, uniqueConstraints = {
	@UniqueConstraint(name = "uq_session_id_file_id", columnNames = {
		"session_id", "file_id"
	})
})
@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileEntity {

	@Id // No automatic generated value as this should be set to uuid from the intric upload file response
	private String fileId;
}
