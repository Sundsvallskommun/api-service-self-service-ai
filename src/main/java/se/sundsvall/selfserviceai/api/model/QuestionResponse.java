package se.sundsvall.selfserviceai.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for question response")
public class QuestionResponse {

	@Schema(description = "The id of the session to be used when interacting with the assistant", examples = "9406e9e3-e2bf-4b5a-9237-2925b396f096")
	private String sessionId;

	@Schema(description = "The question to ask", examples = "What is the answer to the ultimate question of life, the universe and everything?")
	private String question;

	@Schema(description = "Answer to asked question", examples = "42")
	private String answer;

	@ArraySchema(schema = @Schema(description = "Files used when answering asked question", implementation = File.class))
	private List<File> files;

	@ArraySchema(schema = @Schema(description = "References used when answering asked question", implementation = Reference.class))
	private List<Reference> references;

	@Schema(description = "Model used when answering asked question")
	private Model model;

	@Schema(description = "Tools used when answering asked question")
	private Tools tools;
}
