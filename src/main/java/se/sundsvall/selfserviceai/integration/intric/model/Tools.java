package se.sundsvall.selfserviceai.integration.intric.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record Tools(

	@JsonProperty("assistants") List<Assistant> assistants) {
}
