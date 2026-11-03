package com.jirama.postpaid.clientreference.dto;

import java.util.List;

public record ClientReferenceResponse(
    Output mbJIRSetClientReferenceResponse
) {
    public record Output(
        int statusCode,
        String statusReason,
        String operationId,
        String msisdn,
        List<ReferenceDetails> referenceList
    ) {}

    public record ReferenceDetails(
        String clientRef,
        String clientNum,
        String billRef,
        String creationDate
    ) {}
}