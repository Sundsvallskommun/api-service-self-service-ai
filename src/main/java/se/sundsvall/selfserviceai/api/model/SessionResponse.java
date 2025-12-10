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
@Schema(description = "Model for session initiation response")
public class SessionResponse {

	@Schema(description = "The id of the assistant", examples = "becd153d-e1d9-4a4a-9fcf-eb8a157d3f1f")
	private String assistantId;

	@Schema(description = "The id of the session to be used when interacting with the assistant", examples = "9406e9e3-e2bf-4b5a-9237-2925b396f096")
	private String sessionId;
}
