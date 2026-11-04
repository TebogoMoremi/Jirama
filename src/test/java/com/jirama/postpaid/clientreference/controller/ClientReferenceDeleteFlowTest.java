package com.jirama.postpaid.clientreference.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local-registration")
@Transactional
class ClientReferenceDeleteFlowTest {

    private static final String BASE = "/api/v1/postpaid/client-references";
    private static final String OWNER_REQUEST = """
        {"msisdn":"0300000001","clientRef":"DEMO-REF-001"}
        """;

    @Autowired
    private MockMvc mvc;

    @Test
    void onlyOwnerCanDeactivateReferenceAndReadsReflectTheChange() throws Exception {
        mvc.perform(post(BASE + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"msisdn":"0300000002","clientRef":"DEMO-REF-001"}
                    """))
            .andExpect(status().isNotFound());

        mvc.perform(post(BASE + "/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OWNER_REQUEST))
            .andExpect(status().isOk());

        mvc.perform(post(BASE + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OWNER_REQUEST))
            .andExpect(status().isNoContent());

        mvc.perform(post(BASE + "/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OWNER_REQUEST))
            .andExpect(status().isNotFound());

        mvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "mbJIRSetClientReferenceRequest": {
                        "msisdn": "0300000001",
                        "operationType": "getList"
                      }
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$.mbJIRSetClientReferenceResponse.referenceList.length()"
            ).value(0));

        mvc.perform(post(BASE + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OWNER_REQUEST))
            .andExpect(status().isNotFound());
    }
}