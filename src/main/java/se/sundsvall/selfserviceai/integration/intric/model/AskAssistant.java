package se.sundsvall.selfserviceai.integration.intric.model;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record AskAssistant(

	String question) {
}
