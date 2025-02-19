package se.sundsvall.selfserviceai.integration.intric.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder(setterPrefix = "with")
public record FilePublic(

	UUID id,
	String name,
	String mimeType,
	Integer size,
	OffsetDateTime createdAt,
	OffsetDateTime updatedAt) {
}
