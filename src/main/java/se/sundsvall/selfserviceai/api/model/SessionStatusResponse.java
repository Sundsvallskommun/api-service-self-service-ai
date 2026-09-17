package se.sundsvall.selfserviceai.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for session status response")
public class SessionStatusResponse {

	@Schema(description = "The status of the session", examples = "READY", allowableValues = {
		"PENDING", "READY", "FAILED"
	})
	private String status;

	@Schema(description = "Additional detail about the session status", examples = "Session initialization failed")
	private String detail;

	@Schema(description = "The id of the session in Eneo, to be used when interacting with Eneo directly. Null until the first question has been asked, as that is what starts the session in Eneo", examples = "81e73c0b-76dd-455e-bbf3-cc5060aca723")
	private String eneoSessionId;
}
