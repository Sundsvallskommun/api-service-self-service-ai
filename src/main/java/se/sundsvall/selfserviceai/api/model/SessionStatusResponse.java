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
}
