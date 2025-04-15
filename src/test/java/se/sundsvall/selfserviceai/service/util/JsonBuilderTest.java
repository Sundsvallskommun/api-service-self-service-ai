package se.sundsvall.selfserviceai.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.selfserviceai.TestFactory.createAgreements;
import static se.sundsvall.selfserviceai.TestFactory.createCustomer;
import static se.sundsvall.selfserviceai.TestFactory.createInvoice;
import static se.sundsvall.selfserviceai.TestFactory.createMeasurements;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.annotation.resource.Load.ResourceType;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.selfserviceai.integration.intric.mapper.AgreementDecorator;
import se.sundsvall.selfserviceai.integration.intric.mapper.IntricMapper;
import se.sundsvall.selfserviceai.integration.intric.mapper.InvoiceDecorator;
import se.sundsvall.selfserviceai.integration.intric.mapper.MeasurementDecorator;
import se.sundsvall.selfserviceai.integration.intric.model.Message;
import se.sundsvall.selfserviceai.integration.intric.model.SessionPublic;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.IntricModel;
import se.sundsvall.selfserviceai.integration.lime.mapper.LimeMapper;

@ExtendWith(ResourceLoaderExtension.class)
class JsonBuilderTest {

	private static final IntricMapper INTRIC_MAPPER = new IntricMapper();

	@Test
	void toJsonFromIntricModel(@Load(value = "junit/expected-structure-intric-model.json", as = ResourceType.STRING) String expected) {
		final var intricModel = INTRIC_MAPPER.toIntricModel(Map.of("123456789", createCustomer()));

		final var invoice1 = createInvoice();
		final var invoice2 = createInvoice();
		final var invoices = List.of(invoice1, invoice2);

		AgreementDecorator.addAgreements(intricModel.getFacilities(), createAgreements(true));
		MeasurementDecorator.addMeasurements(intricModel.getFacilities(), createMeasurements(true));
		InvoiceDecorator.addInvoices(intricModel.getFacilities(), invoices);

		final var jsonBuilder = new JsonBuilder(new ObjectMapper());
		assertThat(jsonBuilder.toJsonString(intricModel)).isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void toJsonFromNonValidIntricModel() throws Exception {
		final var objectMapperMock = Mockito.mock(ObjectMapper.class);

		when(objectMapperMock.findAndRegisterModules()).thenReturn(objectMapperMock);
		when(objectMapperMock.setDateFormat(any())).thenReturn(objectMapperMock);
		when(objectMapperMock.setSerializationInclusion(any())).thenReturn(objectMapperMock);
		when(objectMapperMock.writeValueAsString(any())).thenThrow(new NullPointerException("test"));

		final var jsonBuilder = new JsonBuilder(objectMapperMock);
		final var e = assertThrows(ThrowableProblem.class, () -> jsonBuilder.toJsonString((IntricModel) null));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getDetail()).isEqualTo("A NullPointerException occurred when serializing intric model object to json");
	}

	@Test
	void toJsonFromLimeChathistorikRequest(@Load(value = "junit/expected-structure-chathistory.json", as = ResourceType.STRING) String expected) {
		final var sessionId = UUID.fromString("ec8fccba-318d-4c67-9251-c7acdb1f8f47");
		final var name = "name";
		final var partyId = "9b4d1641-a868-401b-a1a1-f393e291a80c";
		final var customerNbr = "customerNbr";
		final var request = LimeMapper.toChatHistoryRequest(partyId, customerNbr, SessionPublic.builder()
			.withId(sessionId)
			.withName(name)
			.withMessages(List.of(Message.builder().build()))
			.build());

		final var jsonBuilder = new JsonBuilder(new ObjectMapper());
		assertThat(jsonBuilder.toJsonString(request)).isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void toJsonFromNonValidLimeChathistorikRequest() throws Exception {
		final var objectMapperMock = Mockito.mock(ObjectMapper.class);

		when(objectMapperMock.findAndRegisterModules()).thenReturn(objectMapperMock);
		when(objectMapperMock.setDateFormat(any())).thenReturn(objectMapperMock);
		when(objectMapperMock.setSerializationInclusion(any())).thenReturn(objectMapperMock);
		when(objectMapperMock.writeValueAsString(any())).thenThrow(new NullPointerException("test"));

		final var jsonBuilder = new JsonBuilder(objectMapperMock);
		final var e = assertThrows(ThrowableProblem.class, () -> jsonBuilder.toJsonString((ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest) null));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getDetail()).isEqualTo("A NullPointerException occurred when serializing lime history request object to json");
	}
}
