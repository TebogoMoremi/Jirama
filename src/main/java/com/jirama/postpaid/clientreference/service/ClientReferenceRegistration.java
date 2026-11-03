package com.jirama.postpaid.clientreference.service;

import com.jirama.postpaid.clientreference.partner.JiramaCustomerDetails;
import com.jirama.postpaid.clientreference.partner.JiramaPostpaidClient;
import com.jirama.postpaid.clientreference.repository.ClientReferenceRepository;

public class ClientReferenceRegistration {

    private final JiramaPostpaidClient partnerClient;
    private final ClientReferenceRepository repository;

    public ClientReferenceRegistration(
        JiramaPostpaidClient partnerClient,
        ClientReferenceRepository repository
    ) {
        this.partnerClient = partnerClient;
        this.repository = repository;
    }

    public Candidate prepare(String msisdn, String billRef) {
        if (msisdn == null || msisdn.isBlank()) {
            throw new IllegalArgumentException("msisdn is required");
        }

        if (billRef == null || billRef.isBlank()) {
            throw new IllegalArgumentException("billRef is required");
        }

        JiramaCustomerDetails customer = partnerClient.lookupCustomer(billRef);

        return new Candidate(
            msisdn,
            billRef,
            customer.clientNumber(),
            customer.accountNumber()
        );
    }

    public Candidate register(String msisdn, String billRef) {
        Candidate candidate = prepare(msisdn, billRef);

        repository.insertValidReference(
            candidate.msisdn(),
            candidate.accountNumber(),
            candidate.clientNumber(),
            candidate.billRef()
        );

        return candidate;
    }

    public record Candidate(
        String msisdn,
        String billRef,
        String clientNumber,
        String accountNumber
    ) {}
}