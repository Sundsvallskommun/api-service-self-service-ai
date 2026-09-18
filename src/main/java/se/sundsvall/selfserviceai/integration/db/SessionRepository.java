package se.sundsvall.selfserviceai.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.LockModeType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;

@Repository
@CircuitBreaker(name = "sessionRepository")
public interface SessionRepository extends JpaRepository<SessionEntity, String> {

	Optional<SessionEntity> findBySessionIdAndMunicipalityId(String id, String municipalityId);

	/**
	 * Reads a session and locks its row until the surrounding transaction ends. Used to serialize the initialization of a
	 * session against the removal of it, as the two flows otherwise can act on outdated information about each other.
	 *
	 * @param  id id of the session to read and lock
	 * @return    the session, if it exists
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<SessionEntity> findForUpdateBySessionId(String id);

	SessionEntity getReferenceBySessionIdAndMunicipalityId(String id, String municipalityId);

	/**
	 * Connects a session in Eneo to a session, unless another writer already did. The condition is evaluated in the
	 * database and not on an entity, as the entity for the session is normally already loaded in the persistence context
	 * of the request (open-in-view) and would not be re-read by a locking query, so a check on it could let two concurrent
	 * first questions both believe they won.
	 *
	 * @param  sessionId     id of the session to connect the Eneo session to
	 * @param  eneoSessionId id of the session in Eneo
	 * @return               1 if the Eneo session was connected, 0 if the session does not exist or already has one
	 */
	@Modifying(flushAutomatically = true)
	@Query("update SessionEntity s set s.eneoSessionId = :eneoSessionId where s.sessionId = :sessionId and s.eneoSessionId is null")
	int attachEneoSessionIfMissing(String sessionId, String eneoSessionId);

	/**
	 * Reads the id of the Eneo session connected to a session, straight from the database. A locking read is used since a
	 * plain read in a REPEATABLE READ transaction would return the snapshot from the start of the transaction, and not
	 * what a concurrent writer has committed since.
	 *
	 * @param  sessionId id of the session
	 * @return           the id of the connected Eneo session, or empty if there is none or the session does not exist
	 */
	@Query(value = "select eneo_session_id from session where session_id = :sessionId for update", nativeQuery = true)
	Optional<String> findEneoSessionIdForUpdateBySessionId(String sessionId);

	boolean existsBySessionIdAndMunicipalityId(String id, String municipalityId);

	void deleteBySessionIdAndMunicipalityId(String id, String municipalityId);

	List<SessionEntity> findAllByLastAccessedBeforeOrLastAccessedIsNull(OffsetDateTime timestamp); // Method for returning dangling sessions (not used after provided timestamp) within all municipalities
}
