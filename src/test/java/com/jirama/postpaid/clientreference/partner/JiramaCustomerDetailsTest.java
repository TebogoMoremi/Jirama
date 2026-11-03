package com.jirama.postpaid.clientreference.partner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JiramaCustomerDetailsTest {

    @Test
    void mapsCustomerAndAccountFromPartnerResponse() throws Exception {
        String response = """
            {
              "NumeroClient": 12345678,
              "Compte": "00123456789",
              "NomClient": "Demo Customer"
            }
            """;

        JiramaCustomerDetails details =
            new ObjectMapper().readValue(response, JiramaCustomerDetails.class);

        assertEquals("12345678", details.clientNumber());
        assertEquals("00123456789", details.accountNumber());
    }
}