package se.sundsvall.selfserviceai.integration.eneo.model;

import java.util.List;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record AskAssistant(

	String question,
	List<FilePublic> files) {
}
