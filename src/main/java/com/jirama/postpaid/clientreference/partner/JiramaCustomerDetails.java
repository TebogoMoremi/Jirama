package com.jirama.postpaid.clientreference.partner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JiramaCustomerDetails(
    @JsonProperty("NumeroClient") String clientNumber,
    @JsonProperty("Compte") String accountNumber
) {}