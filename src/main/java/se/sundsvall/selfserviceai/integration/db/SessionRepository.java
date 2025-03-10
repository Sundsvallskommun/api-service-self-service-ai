package se.sundsvall.selfserviceai.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.selfserviceai.integration.db.model.SessionEntity;

@Repository
@CircuitBreaker(name = "sessionRepository")
public interface SessionRepository extends JpaRepository<SessionEntity, String> {

	Optional<SessionEntity> findBySessionIdAndMunicipalityId(String id, String municipalityId);

	SessionEntity getReferenceBySessionIdAndMunicipalityId(String id, String municipalityId);

	boolean existsBySessionIdAndMunicipalityId(String id, String municipalityId);

	void deleteBySessionIdAndMunicipalityId(String id, String municipalityId);

	boolean existsBySessionIdAndMunicipalityIdAndInitializedIsNotNull(String id, String municipalityId); // Method to check if session is ready or not

	List<SessionEntity> findAllByMunicipalityIdAndInitializedIsNull(String municipalityId); // Method to return all currently non initialized sessions within a municipality

	List<SessionEntity> findAllByLastAccessedBeforeOrLastAccessedIsNull(OffsetDateTime timestamp); // Method for returning dangling sessions (not used after provided timestamp) within all municipalities
}
