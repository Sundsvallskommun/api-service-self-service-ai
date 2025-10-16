package se.sundsvall.selfserviceai.integration.eneo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.selfserviceai.integration.eneo.mapper.EneoMapper;
import se.sundsvall.selfserviceai.integration.eneo.model.AskResponse;
import se.sundsvall.selfserviceai.integration.eneo.model.SessionPublic;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.EneoModel;
import se.sundsvall.selfserviceai.service.util.JsonBuilder;

@Component
public class EneoIntegration {

	private static final Logger LOG = LoggerFactory.getLogger(EneoIntegration.class);

	private final EneoClient client;
	private final EneoMapper mapper;
	private final JsonBuilder jsonBuilder;

	EneoIntegration(final EneoClient client, final EneoMapper mapper, final JsonBuilder jsonBuilder) {
		this.client = client;
		this.mapper = mapper;
		this.jsonBuilder = jsonBuilder;
	}

	/**
	 * Asks and initial question to a given assistant
	 *
	 * @param  assistantId The ID of the assistant to ask
	 * @param  input       The question to ask
	 * @return             The response from the assistant or null if problems occurred during assistant interaction
	 */
	public AskResponse askAssistant(final String assistantId, final String input) {
		LOG.debug("Asking assistant initial question");
		final var question = mapper.toAskAssistant(input);
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
			final var question = mapper.toAskAssistant(input, fileReferences);

			LOG.debug("Asking assistant followup question");
			return Optional.of(client.askFollowUp(assistantId, sessionId, question));
		} catch (final Exception e) { // Swallow exception here and let frontend decide how to handle problem
			LOG.warn("Exception when interacting with assistant", e);
			return Optional.empty();
		}
	}

	/**
	 * Get complete history of session interaction
	 *
	 * @param  assistantId The ID of the assistant to get history for
	 * @param  sessionId   The ID of the session to get history for
	 * @return             Complete session history
	 */
	public SessionPublic getSession(final String assistantId, final String sessionId) {
		LOG.debug("Retrieving history from assistant session");
		return client.getSession(assistantId, sessionId);
	}

	/**
	 * Deletes an assistant session from Intric
	 *
	 * @param  assistantId The ID of the assistant that owns the session to delete
	 * @param  sessionId   The ID of the session to delete
	 * @return             Signal if the session was successfully deleted or not
	 */
	public boolean deleteSession(final String assistantId, final String sessionId) {
		try {
			LOG.debug("Deleting assistant session");
			client.deleteSession(assistantId, sessionId);
			LOG.debug("Session deleted");
			return true;
		} catch (final Exception e) {
			LOG.error("Exception when deleting assistant session, manual purge might be needed", e);
			return false;
		}
	}

	/**
	 * Uploads a file to Intric
	 *
	 * @param  eneoModel The file content in the form of a installedBase object to store in intric
	 * @return           The ID of the uploaded file
	 */
	public UUID uploadFile(final EneoModel eneoModel) {
		final var content = jsonBuilder.toJsonString(eneoModel);

		LOG.debug("Uploading file with content '{}'", content);
		return client.uploadFile(mapper.toInformationFile(content)).id();
	}

	/**
	 * Deletes a file from Intric
	 *
	 * @param  id The ID of the file to delete
	 * @return    Signal if the file was successfully deleted or not
	 */
	public boolean deleteFile(final String id) {
		try {
			LOG.debug("Deleting file");
			client.deleteFile(id);
			LOG.debug("File deleted");
			return true;
		} catch (final Exception e) {
			LOG.error("Exception when deleting file, manual purge might be needed", e);
			return false;
		}
	}

}
