package se.sundsvall.selfserviceai.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.sundsvall.selfserviceai.integration.db.model.FileEntity;

@Repository
@CircuitBreaker(name = "fileRepository")
public interface FileRepository extends JpaRepository<FileEntity, String> {
}
