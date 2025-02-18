package se.sundsvall.selfserviceai.integration.intric;

import static se.sundsvall.selfserviceai.integration.intric.IntricConfiguration.CLIENT_ID;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.selfserviceai.integration.intric.model.AskAssistant;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;
import se.sundsvall.selfserviceai.integration.intric.model.FilePublic;

@FeignClient(
	name = CLIENT_ID,
	configuration = IntricConfiguration.class,
	url = "${integration.intric.base-url}")
@CircuitBreaker(name = CLIENT_ID)
interface IntricClient {

	@PostMapping("/api/v1/assistants/{assistantId}/sessions/")
	AskResponse askAssistant(
		@PathVariable("assistantId") final String assistantId,
		final AskAssistant askAssistant);

	@PostMapping("/api/v1/assistants/{assistantId}/sessions/{sessionId}/")
	AskResponse askFollowUp(
		@PathVariable("assistantId") final String assistantId,
		@PathVariable("sessionId") final String sessionId,
		final AskAssistant askAssistant);

	@PostMapping("/api/v1/files/")
	FilePublic uploadFile(final MultipartFile multipartFile);

	@DeleteMapping("/api/v1/files/{id}/")
	void deleteFile(@PathVariable("id") final String id);

}
