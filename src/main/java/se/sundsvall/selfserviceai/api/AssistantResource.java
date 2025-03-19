package se.sundsvall.selfserviceai.api;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.dept44.requestid.RequestId;
import se.sundsvall.selfserviceai.api.model.QuestionResponse;
import se.sundsvall.selfserviceai.api.model.SessionRequest;
import se.sundsvall.selfserviceai.api.model.SessionResponse;
import se.sundsvall.selfserviceai.service.AssistantService;

@RestController
@RequestMapping("/{municipalityId}/session")
@Tag(name = "Assistant session resources", description = "Resources for creating, interacting with and removing assistant sessions")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
@Validated
class AssistantResource {
	private final AssistantService assistantService;

	public AssistantResource(AssistantService assistantService) {
		this.assistantService = assistantService;
	}

	@Operation(summary = "Create assistant session", description = "Resource for creating a new session towards the assistant", responses = {
		@ApiResponse(responseCode = "201", description = "Successful Operation", useReturnTypeSchema = true)
	})
	@PostMapping(produces = APPLICATION_JSON_VALUE)
	ResponseEntity<SessionResponse> createAssistantSession(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@RequestBody @Valid final SessionRequest request) {

		// Create session
		final var sessionId = assistantService.createSession(municipalityId, request.getPartyId());

		// Populate session with information (asynchronously)
		assistantService.populateWithInformation(sessionId, request);

		// Return created with body (as location does not fit here)
		return status(CREATED).body(SessionResponse.builder().withSessionId(sessionId.toString()).build());
	}

	@Operation(summary = "Ask if assistant is ready for interaction", description = "Resource for checking if the assistant is ready to answer questions (i.e. has been fully initialized with data)", responses = {
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
		@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	})
	@GetMapping(value = "/{id}/ready", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<Boolean> isAssistantReady(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "id", description = "Session id", example = "f5211067-b3c7-4394-b84a-aa3fa65507e3") @PathVariable("id") @ValidUuid final String sessionId) {

		return ok(assistantService.isSessionReady(municipalityId, UUID.fromString(sessionId)));
	}

	@Operation(summary = "Interact with assistant", description = "Resource for interacting with the assistant by asking a question", responses = {
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
		@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "204", description = "If no answer could be retrieved")
	})
	@GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<QuestionResponse> askAssistant(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "id", description = "Session id", example = "f5211067-b3c7-4394-b84a-aa3fa65507e3") @PathVariable("id") @ValidUuid final String sessionId,
		@Parameter(name = "question", description = "The question to ask", example = "What is the answer to the ultimate question of life, the universe and everything?") @RequestParam @NotBlank final String question) {

		final var response = assistantService.askQuestion(municipalityId, UUID.fromString(sessionId), question);
		return nonNull(response) ? ok(response) : status(NO_CONTENT).header(CONTENT_TYPE, ALL_VALUE).build();
	}

	@Operation(summary = "Remove assistant session", description = "Resource for removing the assistant session matching sent in id", responses = {
		@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class))),
		@ApiResponse(responseCode = "204", description = "Successful Operation", useReturnTypeSchema = true)
	})
	@DeleteMapping(value = "/{id}", produces = ALL_VALUE)
	ResponseEntity<Void> deleteAssistantSession(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @PathVariable @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "id", description = "Session id", example = "f5211067-b3c7-4394-b84a-aa3fa65507e3") @PathVariable("id") @ValidUuid final String sessionId) {

		// Handle removal of session (asynchronously)
		assistantService.deleteSessionById(municipalityId, UUID.fromString(sessionId), UUID.fromString(RequestId.get()));

		return status(NO_CONTENT).header(CONTENT_TYPE, ALL_VALUE).build();
	}

}
