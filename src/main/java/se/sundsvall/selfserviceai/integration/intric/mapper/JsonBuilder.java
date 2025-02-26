package se.sundsvall.selfserviceai.integration.intric.mapper;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.InstalledBase;

public class JsonBuilder {
	private JsonBuilder() {}

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
		.findAndRegisterModules()
		.setDateFormat(new SimpleDateFormat(ISO_8601_EXTENDED_DATE_FORMAT.getPattern()))
		.setSerializationInclusion(NON_NULL);

	public static String toJsonString(InstalledBase installedBase) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(installedBase);
	}
}
