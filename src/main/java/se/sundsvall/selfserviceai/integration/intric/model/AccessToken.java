package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record AccessToken(

	@JsonProperty("access_token") String accessToken,
	@JsonProperty("token_type") String tokenType) {
}
