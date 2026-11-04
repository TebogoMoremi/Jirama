package com.jirama.postpaid.clientreference.controller;

import com.jirama.postpaid.clientreference.repository.ClientReferenceRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Profile("local-registration")
public class ClientReferenceDeleteController {

    private final ClientReferenceRepository repository;

    public ClientReferenceDeleteController(ClientReferenceRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/api/v1/postpaid/client-references/delete")
    public ResponseEntity<Void> delete(@RequestBody DeleteRequest request) {
        if (request.msisdn() == null || request.msisdn().isBlank()
                || request.clientRef() == null || request.clientRef().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "msisdn and clientRef are required"
            );
        }

        if (!repository.deactivateValidReference(request.msisdn(), request.clientRef())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    public record DeleteRequest(String msisdn, String clientRef) {}
}