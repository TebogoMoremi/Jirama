package com.jirama.postpaid.clientreference;

import com.jirama.postpaid.clientreference.partner.JiramaCustomerDetails;
import com.jirama.postpaid.clientreference.partner.JiramaPostpaidClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local-registration")
@Transactional
class ClientReferenceRegistrationFlowTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private JiramaPostpaidClient partnerClient;

    @Test
    void registersReferenceAndReturnsItThroughGetList() throws Exception {
        when(partnerClient.lookupCustomer("DEMO-INVOICE-005"))
            .thenReturn(new JiramaCustomerDetails("12345678", "00123456789"));

        mvc.perform(post("/api/v1/postpaid/client-references/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"msisdn":"0300000005","billRef":"DEMO-INVOICE-005"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").value("00123456789"));

        mvc.perform(post("/api/v1/postpaid/client-references")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "mbJIRSetClientReferenceRequest": {
                        "msisdn": "0300000005",
                        "operationType": "getList"
                      }
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$.mbJIRSetClientReferenceResponse.referenceList.length()"
            ).value(1))
            .andExpect(jsonPath(
                "$.mbJIRSetClientReferenceResponse.referenceList[0].clientRef"
            ).value("00123456789"))
            .andExpect(jsonPath(
                "$.mbJIRSetClientReferenceResponse.referenceList[0].billRef"
            ).value("DEMO-INVOICE-005"));
    }

    @Test
    void leavesGetListEmptyWhenJiramaReturnsNotFound() throws Exception {
        when(partnerClient.lookupCustomer("DEMO-INVOICE-UNKNOWN"))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mvc.perform(post("/api/v1/postpaid/client-references/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"msisdn":"0300000009","billRef":"DEMO-INVOICE-UNKNOWN"}
                    """))
            .andExpect(status().isBadGateway());

        mvc.perform(post("/api/v1/postpaid/client-references")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "mbJIRSetClientReferenceRequest": {
                        "msisdn": "0300000009",
                        "operationType": "getList"
                      }
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$.mbJIRSetClientReferenceResponse.referenceList.length()"
            ).value(0));
    }
}