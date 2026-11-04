package com.jirama.postpaid.clientreference.controller;

import com.jirama.postpaid.clientreference.partner.JiramaLookupResponseException;
import com.jirama.postpaid.clientreference.service.ClientReferenceRegistration;
import com.jirama.postpaid.clientreference.service.ClientReferenceRegistration.Candidate;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.ResourceAccessException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClientReferenceRegistrationControllerTest {

    private static final String PATH =
        "/api/v1/postpaid/client-references/register";

    private static final String REQUEST = """
        {"msisdn":"0300000004","billRef":"DEMO-INVOICE-004"}
        """;

    private final ClientReferenceRegistration registration =
        mock(ClientReferenceRegistration.class);

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(
        new ClientReferenceRegistrationController(registration)
    ).build();

    @Test
    void returnsCreatedWithRegisteredReference() throws Exception {
        when(registration.register("0300000004", "DEMO-INVOICE-004"))
            .thenReturn(new Candidate(
                "0300000004", "DEMO-INVOICE-004", "12345678", "00123456789"
            ));

        mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(REQUEST))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").value("00123456789"));
    }

    @Test
    void rejectsMissingInvoiceBeforeCallingRegistration() throws Exception {
        mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"msisdn":"0300000004"}
                    """))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(registration);
    }

    @Test
    void returnsConflictForDuplicateReference() throws Exception {
        when(registration.register("0300000004", "DEMO-INVOICE-004"))
            .thenThrow(new DuplicateKeyException("Simulated duplicate"));

        mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(REQUEST))
            .andExpect(status().isConflict());
    }

    @Test
    void returnsServiceUnavailableWhenPartnerCannotBeReached() throws Exception {
        when(registration.register("0300000004", "DEMO-INVOICE-004"))
            .thenThrow(new ResourceAccessException("Simulated timeout"));

        mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(REQUEST))
            .andExpect(status().isServiceUnavailable());
    }

    @Test
void returnsBadGatewayWhenPartnerResponseIsIncomplete() throws Exception {
    when(registration.register("0300000004", "DEMO-INVOICE-004"))
        .thenThrow(new JiramaLookupResponseException("Missing account number"));

    mvc.perform(post(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(REQUEST))
        .andExpect(status().isBadGateway());
}
}