package com.jirama.postpaid.clientreference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GetListIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsOnlyValidReferencesForRequestedPhoneNumber() throws Exception {
        String request = """
                {
                  "mbJIRSetClientReferenceRequest": {
                    "msisdn": "0300000001",
                    "operationType": "getList"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/postpaid/client-references")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.mbJIRSetClientReferenceResponse.msisdn").value("0300000001"))
                .andExpect(jsonPath(
                        "$.mbJIRSetClientReferenceResponse.referenceList.length()").value(1))
                .andExpect(jsonPath(
                        "$.mbJIRSetClientReferenceResponse.referenceList[0].clientRef").value("DEMO-REF-001"));
    }

    @Test
    void returnsEmptyReferenceListWhenNoRecordsMatch() throws Exception {
        String request = """
                {
                  "mbJIRSetClientReferenceRequest": {
                    "msisdn": "0300000009",
                    "operationType": "getList"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/postpaid/client-references")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.mbJIRSetClientReferenceResponse.referenceList.length()").value(0));
    }

    @Test
    void rejectsGetListRequestWhenMsisdnIsBlank() throws Exception {
        String request = """
                {
                  "mbJIRSetClientReferenceRequest": {
                    "msisdn": "",
                    "operationType": "getList"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/postpaid/client-references")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsUnsupportedOperationType() throws Exception {
        String request = """
                {
                  "mbJIRSetClientReferenceRequest": {
                    "msisdn": "0300000001",
                    "operationType": "delete"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/postpaid/client-references")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest());
    }
    @Test
void rejectsRequestWhenWrapperIsMissing() throws Exception {
    mockMvc.perform(post("/api/v1/postpaid/client-references")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());
}
}