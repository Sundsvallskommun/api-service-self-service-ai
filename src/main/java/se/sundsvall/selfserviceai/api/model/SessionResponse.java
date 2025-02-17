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

	@Schema(description = "The id of the session to be used when interacting with the assistant", example = "5a6c3e4e-c320-4006-b448-1fd4121df828")
	private String sessionId;
}
