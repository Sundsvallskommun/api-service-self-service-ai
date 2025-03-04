package se.sundsvall.selfserviceai.integration.intric;

import static se.sundsvall.selfserviceai.integration.intric.mapper.IntricMapper.toAskAssistant;
import static se.sundsvall.selfserviceai.integration.intric.mapper.IntricMapper.toInformationFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import se.sundsvall.selfserviceai.integration.intric.mapper.JsonBuilder;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;

@Component
public class IntricIntegration {

	private static final Logger LOG = LoggerFactory.getLogger(IntricIntegration.class);

	private final IntricClient client;

	IntricIntegration(final IntricClient client) {
		this.client = client;
	}

	/**
	 * Asks and initial question to a given assistant
	 *
	 * @param  assistantId The ID of the assistant to ask
	 * @param  input       The question to ask
	 * @return             The response from the assistant or null if problems occurred during assistant interaction
	 */
	public AskResponse askAssistant(final String assistantId, final String input) {
		LOG.debug("Asking assistant: '{}', question: '{}'", assistantId, input);
		final var question = toAskAssistant(input);
		return client.askAssistant(assistantId, question);
	}

	/**
	 * Asks a followup question to a given assistant
	 *
	 * @param  assistantId    The ID of the assistant to ask
	 * @param  sessionId      The ID of the session to ask in
	 * @param  input          The question to ask
	 * @param  fileReferences List references to files that the assistant shall base its answer on
	 * @return                The response from the assistant or an <code>Optional.empty()</code> if problems occurred
	 *                        during assistant interaction
	 */
	public Optional<AskResponse> askFollowUp(final String assistantId, final String sessionId, final String input, List<String> fileReferences) {
		try {
			final var question = toAskAssistant(input, fileReferences);
			LOG.debug("Ask followup for assistant: '{}', session: '{}', question: '{}'", assistantId, sessionId, input);
			return Optional.of(client.askFollowUp(assistantId, sessionId, question));
		} catch (final Exception e) { // Swallow exception here and let frontend decide how to handle problem
			LOG.warn("Exception when interacting with assistant", e);
			return Optional.empty();
		}
	}

	/**
	 * Deletes an assistant session from Intric
	 *
	 * @param  assistantId The ID of the assistant that owns the session to delete
	 * @param  input       The ID of the session to delete
	 * @return             Signal if the session was successfully deleted or not
	 */
	public boolean deleteSession(final String assistantId, final String sessionId) {
		try {
			LOG.debug("Deleting session: '{}' for assistant: '{}", sessionId, assistantId);
			client.deleteSession(assistantId, sessionId);
			LOG.debug("Session deleted: '{}'", sessionId);
			return true;
		} catch (final Exception e) {
			LOG.warn("Exception when deleting assistant session", e);
			return false;
		}
	}

	/**
	 * Uploads a file to Intric
	 *
	 * @param  installedBase           The file content in the form of a installedBase object to store in intric
	 * @return                         The ID of the uploaded file
	 * @throws JsonProcessingException if json serialization goes south
	 */
	public UUID uploadFile(final InstalledBase installedBase) throws JsonProcessingException {
		LOG.debug("Uploading file");
		final var multipartFile = toInformationFile(JsonBuilder.toJsonString(installedBase));
		return client.uploadFile(multipartFile).id();
	}

	/**
	 * Deletes a file from Intric
	 *
	 * @param  id The ID of the file to delete
	 * @return    Signal if the file was successfully deleted or not
	 */
	public boolean deleteFile(final String id) {
		try {
			LOG.debug("Deleting file: '{}'", id);
			client.deleteFile(id);
			LOG.debug("File deleted: '{}'", id);
			return true;
		} catch (final Exception e) {
			LOG.warn("Exception when deleting file", e);
			return false;
		}
	}

}
