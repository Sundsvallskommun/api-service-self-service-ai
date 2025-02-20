package se.sundsvall.selfserviceai.integration.invoices;

import static generated.se.sundsvall.invoices.InvoiceOrigin.COMMERCIAL;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.BAD_GATEWAY;

import generated.se.sundsvall.invoices.Invoice;
import generated.se.sundsvall.invoices.InvoicesResponse;
import generated.se.sundsvall.invoices.MetaData;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

@ExtendWith(MockitoExtension.class)
class InvoicesIntegrationTest {

	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String PARTY_ID = "partyId";
	private static final String ORGANIZATION_GROUP = "stadsbacken";
	private static final LocalDate TO_DATE = LocalDate.now();
	private static final LocalDate FROM_DATE = LocalDate.now().withDayOfMonth(1).minusMonths(6);

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
	void getInvoicesThrows404Exception() {

		// Arrange
		when(clientMock.getInvoices(eq(MUNICIPALITY_ID), eq(COMMERCIAL), anyInt(), eq(100), eq(PARTY_ID), eq(ORGANIZATION_GROUP), eq(FROM_DATE), eq(TO_DATE))).thenThrow(Problem.valueOf(Status.NOT_FOUND));

		// Act
		final var result = integration.getInvoices(MUNICIPALITY_ID, PARTY_ID);

		// Assert and verify
		verify(clientMock).getInvoices(MUNICIPALITY_ID, COMMERCIAL, 1, 100, PARTY_ID, ORGANIZATION_GROUP, FROM_DATE, TO_DATE);
		assertThat(result).isEmpty();
	}

	@Test
	void getInvoicesThrowsException() {

		// Arrange
		when(clientMock.getInvoices(eq(MUNICIPALITY_ID), eq(COMMERCIAL), anyInt(), eq(100), eq(PARTY_ID), eq(ORGANIZATION_GROUP), eq(FROM_DATE), eq(TO_DATE))).thenThrow(Problem.valueOf(BAD_GATEWAY, "Bad to the bone"));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> integration.getInvoices(MUNICIPALITY_ID, PARTY_ID));

		// Assert and verify
		verify(clientMock).getInvoices(MUNICIPALITY_ID, COMMERCIAL, 1, 100, PARTY_ID, ORGANIZATION_GROUP, FROM_DATE, TO_DATE);
		assertThat(exception.getStatus()).isEqualTo(BAD_GATEWAY);
		assertThat(exception.getMessage()).isEqualTo("Bad Gateway: Bad to the bone");

	}
}
