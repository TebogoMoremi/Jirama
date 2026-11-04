package com.jirama.postpaid.clientreference.service;

import com.jirama.postpaid.clientreference.partner.JiramaCustomerDetails;
import com.jirama.postpaid.clientreference.partner.JiramaPostpaidClient;
import com.jirama.postpaid.clientreference.repository.ClientReferenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClientReferenceRegistrationTest {

    private final JiramaPostpaidClient partnerClient = mock(JiramaPostpaidClient.class);

    private final ClientReferenceRepository repository = mock(ClientReferenceRepository.class);

    private final ClientReferenceRegistration registration = new ClientReferenceRegistration(partnerClient, repository);

    @Test
    void combinesRequestAndPartnerDetails() {
        when(partnerClient.lookupCustomer("DEMO-INVOICE-001"))
                .thenReturn(new JiramaCustomerDetails("12345678", "00123456789"));

        assertEquals(
                new ClientReferenceRegistration.Candidate(
                        "0300000001", "DEMO-INVOICE-001", "12345678", "00123456789"),
                registration.prepare("0300000001", "DEMO-INVOICE-001"));

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsBlankInvoiceBeforeCallingPartnerOrRepository() {
        assertThrows(IllegalArgumentException.class,
                () -> registration.register("0300000001", " "));

        verifyNoInteractions(partnerClient, repository);
    }

    @Test
    void skipsDatabaseWriteWhenPartnerLookupFails() {
        when(partnerClient.lookupCustomer("DEMO-INVOICE-001"))
                .thenThrow(new ResourceAccessException("Simulated timeout"));

        assertThrows(ResourceAccessException.class,
                () -> registration.register("0300000001", "DEMO-INVOICE-001"));

        verifyNoInteractions(repository);
    }

    @Test
    void acceptsReferencesOfTheDocumentedLengths() {
        assertDoesNotThrow(() -> ClientReferenceRegistration.validateReferenceLengths(
                "00123456789", "000123456789012"));
    }

    @Test
    void rejectsClientReferenceWithTenCharacters() {
        assertThrows(IllegalArgumentException.class, () -> ClientReferenceRegistration.validateReferenceLengths(
                "0123456789", "000123456789012"));
    }

    @Test
    void rejectsBillReferenceWithFourteenCharacters() {
        assertThrows(IllegalArgumentException.class, () -> ClientReferenceRegistration.validateReferenceLengths(
                "00123456789", "00123456789012"));
    }
}