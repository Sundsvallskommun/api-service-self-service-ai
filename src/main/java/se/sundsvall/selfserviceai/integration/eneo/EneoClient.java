package se.sundsvall.selfserviceai.integration.eneo;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.selfserviceai.integration.eneo.configuration.EneoConfiguration;
import se.sundsvall.selfserviceai.integration.eneo.model.AskAssistant;
import se.sundsvall.selfserviceai.integration.eneo.model.AskResponse;
import se.sundsvall.selfserviceai.integration.eneo.model.FilePublic;
import se.sundsvall.selfserviceai.integration.eneo.model.SessionPublic;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static se.sundsvall.selfserviceai.integration.eneo.configuration.EneoConfiguration.CLIENT_ID;

@FeignClient(
	name = CLIENT_ID,
	configuration = EneoConfiguration.class,
	url = "${integration.eneo.url}")
@CircuitBreaker(name = CLIENT_ID)
interface EneoClient {

	@PostMapping(value = "/assistants/{assistantId}/sessions/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	AskResponse askAssistant(
		@PathVariable("assistantId") final String assistantId,
		@RequestBody final AskAssistant askAssistant);

	@PostMapping(value = "/assistants/{assistantId}/sessions/{sessionId}/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	AskResponse askFollowUp(
		@PathVariable("assistantId") final String assistantId,
		@PathVariable("sessionId") final String sessionId,
		@RequestBody final AskAssistant askAssistant);

	@GetMapping("/assistants/{assistantId}/sessions/{sessionId}/")
	SessionPublic getSession(
		@PathVariable("assistantId") final String assistantId,
		@PathVariable("sessionId") final String sessionId);

	@DeleteMapping("/assistants/{assistantId}/sessions/{sessionId}/")
	void deleteSession(
		@PathVariable("assistantId") final String assistantId,
		@PathVariable("sessionId") final String sessionId);

	@PostMapping(value = "/files/", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
	FilePublic uploadFile(@RequestPart("upload_file") final MultipartFile multipartFile);

	@DeleteMapping("/files/{id}/")
	void deleteFile(@PathVariable("id") final String id);
}
