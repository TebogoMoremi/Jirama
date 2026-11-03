package com.jirama.postpaid.clientreference.dto;

public record ClientReferenceRequest(
    Input mbJIRSetClientReferenceRequest
) {
    public record Input(
        String msisdn,
        String operationType,
        String secretCode,
        String clientRef,
        String billRef,
        String channel,
        String lang
    ) {}
}