package com.jirama.postpaid.clientreference.partner;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import java.time.Duration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class JiramaPostpaidClient {

    private final RestClient restClient;

    public JiramaPostpaidClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public JiramaCustomerDetails lookupCustomer(String invoiceReference) {
        JiramaCustomerDetails details = restClient.post()
                .uri("/jderest/orchestrator/ORCH_WS_1_3_ClientPostpaid")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("FactureReference", invoiceReference))
                .retrieve()
                .body(JiramaCustomerDetails.class);

        if (details == null
                || details.clientNumber() == null || details.clientNumber().isBlank()
                || details.accountNumber() == null || details.accountNumber().isBlank()) {
            throw new JiramaLookupResponseException(
    "JIRAMA customer lookup response is missing customer or account details"
);
        }

        return details;
    }

    public static JiramaPostpaidClient withTimeouts(String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        RestClient client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

        return new JiramaPostpaidClient(client);
    }
}