package se.sundsvall.selfserviceai.integration.intric;

import static se.sundsvall.selfserviceai.integration.intric.mapper.IntricMapper.toAskAssistant;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.selfserviceai.integration.intric.model.AskResponse;

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
		try {
			final var question = toAskAssistant(input);
			LOG.debug("Asking assistant: '{}', question: '{}'", assistantId, input);
			return client.askAssistant(assistantId, question);
		} catch (final Exception e) {
			LOG.debug("Failed to ask assistant: '{}', question: '{}'", assistantId, input);
			return null;
		}
	}

	/**
	 * Asks a followup question to a given assistant
	 *
	 * @param  assistantId    The ID of the assistant to ask
	 * @param  sessionId      The ID of the session to ask in
	 * @param  input          The question to ask
	 * @param  fileReferences List references to files that the assistant shall base its answer on
	 * @return                The response from the assistant or null if problems occurred during assistant interaction
	 */
	public AskResponse askFollowUp(final String assistantId, final String sessionId, final String input, List<String> fileReferences) {
		try {
			final var question = toAskAssistant(input, fileReferences);
			LOG.debug("Ask followup for assistant: '{}', session: '{}', question: '{}'", assistantId, sessionId, input);
			return client.askFollowUp(assistantId, sessionId, question);
		} catch (final Exception e) {
			LOG.debug("Failed to ask followup for assistant: '{}', session: '{}', question: '{}'", assistantId, sessionId, input);
			return null;
		}
	}

	/**
	 * Uploads a file to Intric
	 *
	 * @param  multipartFile The file to upload
	 * @return               The ID of the uploaded file or null if problems occurred during upload
	 */
	public UUID uploadFile(final MultipartFile multipartFile) {
		try {
			LOG.debug("Uploading file");
			return client.uploadFile(multipartFile).id();
		} catch (final Exception e) {
			LOG.debug("Failed to upload file");
			return null;
		}
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
			LOG.debug("Failed to delete file: '{}'", id);
			return false;
		}
	}

}
