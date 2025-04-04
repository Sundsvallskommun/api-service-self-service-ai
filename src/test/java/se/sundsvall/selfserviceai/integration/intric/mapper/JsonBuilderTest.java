package se.sundsvall.selfserviceai.integration.intric.mapper;

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
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;

@ExtendWith(ResourceLoaderExtension.class)
class JsonBuilderTest {

	private static final IntricMapper INTRIC_MAPPER = new IntricMapper();

	@Test
	void toJson(@Load(value = "junit/expected-structure.json", as = Load.ResourceType.STRING) String expected) {
		final var intricModel = INTRIC_MAPPER.toIntricModel(Map.of("123456789", createCustomer()));

		var invoice1 = createInvoice();
		var invoice2 = createInvoice();
		var invoices = List.of(invoice1, invoice2);

		AgreementDecorator.addAgreements(intricModel.getFacilities(), createAgreements(true));
		MeasurementDecorator.addMeasurements(intricModel.getFacilities(), createMeasurements(true));
		InvoiceDecorator.addInvoices(intricModel.getFacilities(), invoices);

		final var jsonBuilder = new JsonBuilder(new ObjectMapper());
		assertThat(jsonBuilder.toJsonString(intricModel)).isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void toJsonForNonValidJson() throws Exception {
		final var objectMapperMock = Mockito.mock(ObjectMapper.class);

		when(objectMapperMock.findAndRegisterModules()).thenReturn(objectMapperMock);
		when(objectMapperMock.setDateFormat(any())).thenReturn(objectMapperMock);
		when(objectMapperMock.setSerializationInclusion(any())).thenReturn(objectMapperMock);
		when(objectMapperMock.writeValueAsString(any())).thenThrow(new NullPointerException("test"));

		final var jsonBuilder = new JsonBuilder(objectMapperMock);
		final var e = assertThrows(ThrowableProblem.class, () -> jsonBuilder.toJsonString(null));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getDetail()).isEqualTo("A NullPointerException occurred when serializing installed base object to json");
	}
}
