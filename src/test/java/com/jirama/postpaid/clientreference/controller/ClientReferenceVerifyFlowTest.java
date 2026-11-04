package com.jirama.postpaid.clientreference.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local-registration")
class ClientReferenceVerifyFlowTest {

    @Autowired
    private MockMvc mvc;

    @ParameterizedTest
    @CsvSource({
        "0300000001, DEMO-REF-001, 200",
        "0300000001, DEMO-REF-002, 404",
        "0300000001, DEMO-REF-003, 404",
        "0300000001, DEMO-REF-UNKNOWN, 404",
        "0300000001, '', 400"
    })
    void returnsExpectedStatus(
        String msisdn, String clientRef, int expectedStatus
    ) throws Exception {
        String request = """
            {"msisdn":"%s","clientRef":"%s"}
            """.formatted(msisdn, clientRef);

        mvc.perform(post("/api/v1/postpaid/client-references/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().is(expectedStatus));
    }
}