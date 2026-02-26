package se.sundsvall.selfserviceai.service.util;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.EneoModel;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
public class JsonBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(JsonBuilder.class);
	private static final String ENEO_MODEL_SERIALIZATION_ERROR_MESSAGE = "A %s occurred when serializing Eneo model object to json";
	private static final String LIME_HISTORY_SERIALIZATION_ERROR_MESSAGE = "A %s occurred when serializing lime history request object to json";

	private final ObjectMapper objectMapper;

	public JsonBuilder() {
		this(JsonMapper.builder()
			.defaultDateFormat(new SimpleDateFormat(ISO_8601_EXTENDED_DATE_FORMAT.getPattern()))
			.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(NON_NULL))
			.build());
	}

	JsonBuilder(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public String toJsonString(final EneoModel eneoModel) {
		try {
			return objectMapper.writeValueAsString(eneoModel);
		} catch (final Exception e) {
			final var formattedError = ENEO_MODEL_SERIALIZATION_ERROR_MESSAGE.formatted(e.getClass().getSimpleName());
			LOG.error(formattedError, e);
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, formattedError);
		}
	}

	public String toJsonString(final ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest limeHistory) {
		try {
			return objectMapper.writeValueAsString(limeHistory);
		} catch (final Exception e) {
			final var formattedError = LIME_HISTORY_SERIALIZATION_ERROR_MESSAGE.formatted(e.getClass().getSimpleName());
			LOG.error(formattedError, e);
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, formattedError);
		}
	}
}
