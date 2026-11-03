package com.jirama.postpaid.clientreference.controller;

import com.jirama.postpaid.clientreference.dto.ClientReferenceRequest;
import com.jirama.postpaid.clientreference.dto.ClientReferenceResponse;
import com.jirama.postpaid.clientreference.service.ClientReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ClientReferenceController {

    private final ClientReferenceService clientReferenceService;

    public ClientReferenceController(
        ClientReferenceService clientReferenceService
    ) {
        this.clientReferenceService = clientReferenceService;
    }

    @PostMapping("/api/v1/postpaid/client-references")
    public ClientReferenceResponse getList(
        @RequestBody ClientReferenceRequest request
    ) {
        ClientReferenceRequest.Input input =
            request.mbJIRSetClientReferenceRequest();

        if (input == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "mbJIRSetClientReferenceRequest is required"
            );
        }

        String msisdn = input.msisdn();

        if (msisdn == null || msisdn.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "msisdn is required"
            );
        }

        if (!"getList".equals(input.operationType())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "operationType must be getList"
            );
        }

        return clientReferenceService.getList(msisdn);
    }
}