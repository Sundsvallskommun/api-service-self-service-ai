package se.sundsvall.selfserviceai.integration.intric;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.selfserviceai.integration.intric.model.AskAssistant;
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
	 * @return             The response from the assistant
	 */
	public AskResponse askAssistant(final String assistantId, final String input) {
		try {
			var question = AskAssistant.builder()
				.withQuestion(input)
				.build();
			LOG.debug("Asking assistant: '{}', question: '{}'", assistantId, input);
			return client.askAssistant(assistantId, question);
		} catch (Exception e) {
			LOG.debug("Failed to ask assistant: '{}', question: '{}'", assistantId, input);
			return AskResponse.builder()
				.withSessionId(null)
				.withQuestion(input)
				.withAnswer("")
				.build();
		}
	}

	/**
	 * Asks a followup question to a given assistant
	 *
	 * @param  assistantId The ID of the assistant to ask
	 * @param  sessionId   The ID of the session to ask in
	 * @param  input       The question to ask
	 * @return             The response from the assistant
	 */
	public AskResponse askFollowUp(final String assistantId, final String sessionId, final String input) {
		try {
			var question = AskAssistant.builder()
				.withQuestion(input)
				.build();
			LOG.debug("Ask followup for assistant: '{}', session: '{}', question: '{}'", assistantId, sessionId, input);
			return client.askFollowUp(assistantId, sessionId, question);
		} catch (Exception e) {
			LOG.debug("Failed to ask followup for assistant: '{}', session: '{}', question: '{}'", assistantId, sessionId, input);
			return AskResponse.builder()
				.withSessionId(UUID.fromString(sessionId))
				.withQuestion(input)
				.withAnswer("")
				.build();
		}
	}

	/**
	 * Uploads a file to Intric
	 *
	 * @param  multipartFile The file to upload
	 * @return               The ID of the uploaded file
	 */
	public UUID uploadFile(final MultipartFile multipartFile) {
		try {
			LOG.debug("Uploading file");
			return client.uploadFile(multipartFile).id();
		} catch (Exception e) {
			LOG.debug("Failed to upload file");
			return null;
		}
	}

	/**
	 * Deletes a file from Intric
	 *
	 * @param id The ID of the file to delete
	 */
	public void deleteFile(final String id) {
		try {
			LOG.debug("Deleting file: '{}'", id);
			client.deleteFile(id);
			LOG.debug("File deleted: '{}'", id);
		} catch (Exception e) {
			LOG.debug("Failed to delete file: '{}'", id);
		}
	}

}
