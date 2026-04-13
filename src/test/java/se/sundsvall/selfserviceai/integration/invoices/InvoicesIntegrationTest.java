package se.sundsvall.selfserviceai.integration.invoices;

import generated.se.sundsvall.invoices.Invoice;
import generated.se.sundsvall.invoices.InvoiceDetail;
import generated.se.sundsvall.invoices.InvoiceDetailsResponse;
import generated.se.sundsvall.invoices.InvoicesResponse;
import generated.se.sundsvall.invoices.MetaData;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static generated.se.sundsvall.invoices.InvoiceOrigin.COMMERCIAL;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicesIntegrationTest {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String PARTY_ID = "partyId";
	private static final String ORGANIZATION_GROUP = "stadsbacken";
	private static final String TO_DATE = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
	private static final String FROM_DATE = LocalDate.now().withDayOfMonth(1).minusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE);

	@Mock
	private InvoicesClient clientMock;

	@Mock
	private InvoicesResponse responseMock;

	@Mock
	private MetaData metaDataMock;

	@InjectMocks
	private InvoicesIntegration integration;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	void getInvoices() {

		// Arrange
		final var invoice1 = new Invoice().invoiceNumber("1");
		final var invoice2 = new Invoice().invoiceNumber("2");

		when(clientMock.getInvoices(eq(MUNICIPALITY_ID), eq(COMMERCIAL), anyInt(), eq(100), eq(PARTY_ID), eq(ORGANIZATION_GROUP), eq(FROM_DATE), eq(TO_DATE))).thenReturn(responseMock);
		when(responseMock.getInvoices()).thenReturn(List.of(invoice1), List.of(invoice2));
		when(responseMock.getMeta()).thenReturn(metaDataMock);
		when(metaDataMock.getPage()).thenReturn(1, 2);
		when(metaDataMock.getTotalPages()).thenReturn(2);

		// Act
		final var result = integration.getInvoices(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		verify(clientMock).getInvoices(MUNICIPALITY_ID, COMMERCIAL, 1, 100, PARTY_ID, ORGANIZATION_GROUP, FROM_DATE, TO_DATE);
		verify(clientMock).getInvoices(MUNICIPALITY_ID, COMMERCIAL, 2, 100, PARTY_ID, ORGANIZATION_GROUP, FROM_DATE, TO_DATE);
		assertThat(result).containsExactlyInAnyOrder(invoice1, invoice2);
	}

	@Test
	void getInvoicesReturnsEmptyResponse() {

		// Arrange
		when(clientMock.getInvoices(eq(MUNICIPALITY_ID), eq(COMMERCIAL), anyInt(), eq(100), eq(PARTY_ID), eq(ORGANIZATION_GROUP), eq(FROM_DATE), eq(TO_DATE))).thenReturn(responseMock);
		when(responseMock.getInvoices()).thenReturn(emptyList());
		when(responseMock.getMeta()).thenReturn(metaDataMock);
		when(metaDataMock.getPage()).thenReturn(0);
		when(metaDataMock.getTotalPages()).thenReturn(0);

		// Act
		final var result = integration.getInvoices(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		verify(clientMock).getInvoices(MUNICIPALITY_ID, COMMERCIAL, 1, 100, PARTY_ID, ORGANIZATION_GROUP, FROM_DATE, TO_DATE);
		assertThat(result).isEmpty();
	}

	@Test
	void getInvoicesReturnsNullResponse() {

		// Arrange
		when(clientMock.getInvoices(eq(MUNICIPALITY_ID), eq(COMMERCIAL), anyInt(), eq(100), eq(PARTY_ID), eq(ORGANIZATION_GROUP), eq(FROM_DATE), eq(TO_DATE))).thenReturn(null);

		// Act
		final var result = integration.getInvoices(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		verify(clientMock).getInvoices(MUNICIPALITY_ID, COMMERCIAL, 1, 100, PARTY_ID, ORGANIZATION_GROUP, FROM_DATE, TO_DATE);
		assertThat(result).isEmpty();
	}

	@Test
	void getInvoiceDetails() {

		// Arrange
		final var invoice = new Invoice().organizationNumber("orgNr").invoiceNumber("invNr");
		final var detail = new InvoiceDetail();
		final var detailsResponse = new InvoiceDetailsResponse().details(List.of(detail));

		when(clientMock.getInvoiceDetails(MUNICIPALITY_ID, "orgNr", "invNr")).thenReturn(detailsResponse);

		// Act
		final var result = integration.getInvoiceDetails(MUNICIPALITY_ID, invoice);

		// Assert and verify
		verify(clientMock).getInvoiceDetails(MUNICIPALITY_ID, "orgNr", "invNr");
		assertThat(result).containsExactly(detail);
	}

	@Test
	void getInvoiceDetailsReturnsNullResponse() {

		// Arrange
		final var invoice = new Invoice().organizationNumber("orgNr").invoiceNumber("invNr");

		when(clientMock.getInvoiceDetails(MUNICIPALITY_ID, "orgNr", "invNr")).thenReturn(null);

		// Act
		final var result = integration.getInvoiceDetails(MUNICIPALITY_ID, invoice);

		// Assert and verify
		verify(clientMock).getInvoiceDetails(MUNICIPALITY_ID, "orgNr", "invNr");
		assertThat(result).isEmpty();
	}

	@Test
	void getInvoiceDetailsReturnsEmptyListWhenClientThrows() {

		// Arrange — invoices that lack details (e.g. samlingsfakturor) must not break the whole flow
		final var invoice = new Invoice().organizationNumber("orgNr").invoiceNumber("invNr");

		when(clientMock.getInvoiceDetails(MUNICIPALITY_ID, "orgNr", "invNr")).thenThrow(new RuntimeException("No details available"));

		// Act
		final var result = integration.getInvoiceDetails(MUNICIPALITY_ID, invoice);

		// Assert and verify
		verify(clientMock).getInvoiceDetails(MUNICIPALITY_ID, "orgNr", "invNr");
		assertThat(result).isEmpty();
	}
}
