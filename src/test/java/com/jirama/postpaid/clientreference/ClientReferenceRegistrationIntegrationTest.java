package com.jirama.postpaid.clientreference;

import com.jirama.postpaid.clientreference.partner.JiramaCustomerDetails;
import com.jirama.postpaid.clientreference.partner.JiramaPostpaidClient;
import com.jirama.postpaid.clientreference.repository.ClientReferenceRepository;
import com.jirama.postpaid.clientreference.service.ClientReferenceRegistration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ClientReferenceRegistrationIntegrationTest {

    @Autowired
    private ClientReferenceRepository repository;

    @Test
    void registersReferenceAndRejectsDuplicate() {
        JiramaPostpaidClient partnerClient = mock(JiramaPostpaidClient.class);
        when(partnerClient.lookupCustomer("DEMO-INVOICE-004"))
            .thenReturn(new JiramaCustomerDetails("12345678", "00123456789"));

        var registration = new ClientReferenceRegistration(partnerClient, repository);
        var candidate = registration.register("0300000004", "DEMO-INVOICE-004");

        var references = repository.findValidByMsisdn(candidate.msisdn());
        assertEquals(1, references.size());

        var saved = references.get(0);
        assertEquals("00123456789", saved.clientRef());
        assertEquals("12345678", saved.clientNum());
        assertEquals("DEMO-INVOICE-004", saved.billRef());

        assertThrows(DuplicateKeyException.class, () ->
            registration.register("0300000004", "DEMO-INVOICE-004")
        );

        assertEquals(1, repository.findValidByMsisdn(candidate.msisdn()).size());
    }
}