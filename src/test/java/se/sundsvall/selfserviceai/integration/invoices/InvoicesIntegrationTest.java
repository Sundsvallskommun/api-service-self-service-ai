package se.sundsvall.selfserviceai.integration.invoices;

import generated.se.sundsvall.invoices.CustomerInvoice;
import generated.se.sundsvall.invoices.CustomerInvoicesResponse;
import generated.se.sundsvall.invoices.MetaData;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.selfserviceai.integration.invoices.configuration.InvoicesProperties;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicesIntegrationTest {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String PARTY_ID = "partyId";
	private static final List<String> PARTY_IDS = List.of(PARTY_ID);
	private static final List<String> ORGANIZATION_NUMBERS = List.of("5564786647", "5565027223");
	private static final String PERIOD_TO = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
	private static final String PERIOD_FROM = LocalDate.now().withDayOfMonth(1).minusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE);

	@Mock
	private InvoicesClient clientMock;

	@Mock
	private CustomerInvoicesResponse responseMock;

	@Mock
	private MetaData metaDataMock;

	@Mock
	private InvoicesProperties propertiesMock;

	private InvoicesIntegration integration;

	@BeforeEach
	void setUp() {
		lenient().when(propertiesMock.organizationNumbers()).thenReturn(ORGANIZATION_NUMBERS);
		integration = new InvoicesIntegration(clientMock, propertiesMock);
	}

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void getInvoices() {

		// Arrange
		final var invoice1 = new CustomerInvoice().invoiceNumber("1");
		final var invoice2 = new CustomerInvoice().invoiceNumber("2");

		when(clientMock.getInvoicesForCustomer(eq(MUNICIPALITY_ID), eq(PARTY_IDS), eq(ORGANIZATION_NUMBERS), eq(PERIOD_FROM), eq(PERIOD_TO), anyInt(), eq(100))).thenReturn(responseMock);
		when(responseMock.getInvoices()).thenReturn(List.of(invoice1), List.of(invoice2));
		when(responseMock.getMeta()).thenReturn(metaDataMock);
		when(metaDataMock.getPage()).thenReturn(1, 2);
		when(metaDataMock.getTotalPages()).thenReturn(2);

		// Act
		final var result = integration.getInvoices(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		verify(clientMock).getInvoicesForCustomer(MUNICIPALITY_ID, PARTY_IDS, ORGANIZATION_NUMBERS, PERIOD_FROM, PERIOD_TO, 1, 100);
		verify(clientMock).getInvoicesForCustomer(MUNICIPALITY_ID, PARTY_IDS, ORGANIZATION_NUMBERS, PERIOD_FROM, PERIOD_TO, 2, 100);
		assertThat(result).containsExactlyInAnyOrder(invoice1, invoice2);
	}

	@Test
	void getInvoicesReturnsEmptyResponse() {

		// Arrange
		when(clientMock.getInvoicesForCustomer(eq(MUNICIPALITY_ID), eq(PARTY_IDS), eq(ORGANIZATION_NUMBERS), eq(PERIOD_FROM), eq(PERIOD_TO), anyInt(), eq(100))).thenReturn(responseMock);
		when(responseMock.getInvoices()).thenReturn(emptyList());
		when(responseMock.getMeta()).thenReturn(metaDataMock);
		when(metaDataMock.getPage()).thenReturn(0);
		when(metaDataMock.getTotalPages()).thenReturn(0);

		// Act
		final var result = integration.getInvoices(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		verify(clientMock).getInvoicesForCustomer(MUNICIPALITY_ID, PARTY_IDS, ORGANIZATION_NUMBERS, PERIOD_FROM, PERIOD_TO, 1, 100);
		assertThat(result).isEmpty();
	}

	@Test
	void getInvoicesReturnsNullResponse() {

		// Arrange
		when(clientMock.getInvoicesForCustomer(eq(MUNICIPALITY_ID), eq(PARTY_IDS), eq(ORGANIZATION_NUMBERS), eq(PERIOD_FROM), eq(PERIOD_TO), anyInt(), eq(100))).thenReturn(null);

		// Act
		final var result = integration.getInvoices(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		verify(clientMock).getInvoicesForCustomer(MUNICIPALITY_ID, PARTY_IDS, ORGANIZATION_NUMBERS, PERIOD_FROM, PERIOD_TO, 1, 100);
		assertThat(result).isEmpty();
	}
}
