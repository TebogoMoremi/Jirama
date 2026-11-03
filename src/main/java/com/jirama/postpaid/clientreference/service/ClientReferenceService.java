package com.jirama.postpaid.clientreference.service;

import com.jirama.postpaid.clientreference.dto.ClientReferenceResponse;
import com.jirama.postpaid.clientreference.dto.ClientReferenceResponse.Output;
import com.jirama.postpaid.clientreference.dto.ClientReferenceResponse.ReferenceDetails;
import com.jirama.postpaid.clientreference.repository.ClientReferenceRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ClientReferenceService {

    private final ClientReferenceRepository repository;

    public ClientReferenceService(ClientReferenceRepository repository) {
        this.repository = repository;
    }

    public ClientReferenceResponse getList(String msisdn) {
        List<ReferenceDetails> references = repository.findValidByMsisdn(msisdn);

        return new ClientReferenceResponse(new Output(
            200,
            "Client reference lookup completed",
            UUID.randomUUID().toString(),
            msisdn,
            references
        ));
    }
}