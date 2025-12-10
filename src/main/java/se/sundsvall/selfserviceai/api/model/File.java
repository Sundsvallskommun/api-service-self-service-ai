package se.sundsvall.selfserviceai.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for file information")
public class File {

	@Schema(description = "Id of the file", examples = "9406e9e3-e2bf-4b5a-9237-2925b396f096")
	private String id;

	@Schema(description = "Readable name for the file", examples = "file-name.json")
	private String name;

	@Schema(description = "Mime type for the file", examples = "application/json")
	private String mimeType;

	@Schema(description = "Size of the file", examples = "12345")
	private int size;

	@Schema(description = "Timestamp when file was created")
	private OffsetDateTime createdAt;

	@Schema(description = "Timestamp when file was last updated")
	private OffsetDateTime updatedAt;

	@Schema(description = "Transcription of file", examples = "transcription")
	private String transcription;
}
