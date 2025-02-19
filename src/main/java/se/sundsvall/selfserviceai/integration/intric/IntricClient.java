package se.sundsvall.selfserviceai.integration.intric;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static se.sundsvall.selfserviceai.integration.intric.IntricConfiguration.CLIENT_ID;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@PostMapping(value = "/api/v1/assistants/{assistantId}/sessions/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	AskResponse askAssistant(
		@PathVariable("assistantId") final String assistantId,
		@RequestBody final AskAssistant askAssistant);

	@PostMapping(value = "/api/v1/assistants/{assistantId}/sessions/{sessionId}/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	AskResponse askFollowUp(
		@PathVariable("assistantId") final String assistantId,
		@PathVariable("sessionId") final String sessionId,
		@RequestBody final AskAssistant askAssistant);

	@PostMapping(value = "/api/v1/files/", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
	FilePublic uploadFile(final MultipartFile multipartFile);

	@DeleteMapping("/api/v1/files/{id}/")
	void deleteFile(@PathVariable("id") final String id);

}
