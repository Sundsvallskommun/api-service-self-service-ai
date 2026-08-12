package se.sundsvall.selfserviceai.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.LockModeType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

	boolean existsBySessionIdAndMunicipalityId(String id, String municipalityId);

	void deleteBySessionIdAndMunicipalityId(String id, String municipalityId);

	List<SessionEntity> findAllByLastAccessedBeforeOrLastAccessedIsNull(OffsetDateTime timestamp); // Method for returning dangling sessions (not used after provided timestamp) within all municipalities
}
