package se.sundsvall.selfserviceai.service.util;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.EneoModel;

@Component
public class JsonBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(JsonBuilder.class);
	private static final String INTRIC_MODEL_SERIALIZATION_ERROR_MESSAGE = "A %s occurred when serializing intric model object to json";
	private static final String LIME_HISTORY_SERIALIZATION_ERROR_MESSAGE = "A %s occurred when serializing lime history request object to json";

	private final ObjectMapper objectMapper;

	public JsonBuilder(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper
			.findAndRegisterModules()
			.setDateFormat(new SimpleDateFormat(ISO_8601_EXTENDED_DATE_FORMAT.getPattern()))
			.setSerializationInclusion(NON_NULL);
	}

	public String toJsonString(EneoModel eneoModel) {
		try {
			return objectMapper.writeValueAsString(eneoModel);
		} catch (final Exception e) {
			final var formattedError = INTRIC_MODEL_SERIALIZATION_ERROR_MESSAGE.formatted(e.getClass().getSimpleName());
			LOG.error(formattedError, e);
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, formattedError);
		}
	}

	public String toJsonString(ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest limeHistory) {
		try {
			return objectMapper.writeValueAsString(limeHistory);
		} catch (final Exception e) {
			final var formattedError = LIME_HISTORY_SERIALIZATION_ERROR_MESSAGE.formatted(e.getClass().getSimpleName());
			LOG.error(formattedError, e);
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, formattedError);
		}
	}
}
