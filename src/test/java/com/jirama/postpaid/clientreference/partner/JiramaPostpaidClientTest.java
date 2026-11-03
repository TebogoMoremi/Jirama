package com.jirama.postpaid.clientreference.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.net.SocketTimeoutException;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class JiramaPostpaidClientTest {

    @Test
    void sendsInvoiceReferenceAndReadsCustomerDetails() {
        RestClient.Builder builder = RestClient.builder()
            .baseUrl("http://jirama.test");

        MockRestServiceServer server =
            MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(
                "http://jirama.test/jderest/orchestrator/ORCH_WS_1_3_ClientPostpaid"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().json("""
                {"FactureReference":"DEMO-INVOICE-001"}
                """))
            .andRespond(withSuccess("""
                {
                  "NumeroClient": 12345678,
                  "Compte": "00123456789"
                }
                """, MediaType.APPLICATION_JSON));

        JiramaPostpaidClient client = new JiramaPostpaidClient(builder.build());

        JiramaCustomerDetails result = client.lookupCustomer("DEMO-INVOICE-001");

        assertEquals("12345678", result.clientNumber());
        assertEquals("00123456789", result.accountNumber());
        server.verify();
    }
    @Test
void rejectsSuccessfulResponseWhenAccountNumberIsMissing() {
    RestClient.Builder builder = RestClient.builder()
        .baseUrl("http://jirama.test");

    MockRestServiceServer server =
        MockRestServiceServer.bindTo(builder).build();

    server.expect(requestTo(
            "http://jirama.test/jderest/orchestrator/ORCH_WS_1_3_ClientPostpaid"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess("""
            {"NumeroClient": 12345678}
            """, MediaType.APPLICATION_JSON));

    JiramaPostpaidClient client = new JiramaPostpaidClient(builder.build());

    IllegalStateException error = assertThrows(
        IllegalStateException.class,
        () -> client.lookupCustomer("DEMO-INVOICE-001")
    );

    assertEquals(
        "JIRAMA customer lookup response is missing customer or account details",
        error.getMessage()
    );
    server.verify();
}
@Test
void reportsTimeoutAsTransportFailure() {
    RestClient.Builder builder = RestClient.builder()
        .baseUrl("http://jirama.test");

    MockRestServiceServer server =
        MockRestServiceServer.bindTo(builder).build();

    server.expect(requestTo(
            "http://jirama.test/jderest/orchestrator/ORCH_WS_1_3_ClientPostpaid"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withException(new SocketTimeoutException("Read timed out")));

    JiramaPostpaidClient client = new JiramaPostpaidClient(builder.build());

    ResourceAccessException error = assertThrows(
        ResourceAccessException.class,
        () -> client.lookupCustomer("DEMO-INVOICE-001")
    );

    assertInstanceOf(SocketTimeoutException.class, error.getCause());
    server.verify();
}
}
