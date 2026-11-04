package com.jirama.postpaid.clientreference.controller;

import com.jirama.postpaid.clientreference.partner.JiramaLookupResponseException;
import org.springframework.web.client.RestClientResponseException;
import com.jirama.postpaid.clientreference.service.ClientReferenceRegistration;
import com.jirama.postpaid.clientreference.service.ClientReferenceRegistration.Candidate;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Profile("local-registration")
public class ClientReferenceRegistrationController {

    private final ClientReferenceRegistration registration;

    public ClientReferenceRegistrationController(
            ClientReferenceRegistration registration) {
        this.registration = registration;
    }

    @PostMapping("/api/v1/postpaid/client-references/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Candidate register(@RequestBody RegistrationRequest request) {
        if (request.msisdn() == null || request.msisdn().isBlank()
                || request.billRef() == null || request.billRef().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "msisdn and billRef are required");
        }

        try {
            return registration.register(request.msisdn(), request.billRef());
        } catch (DuplicateKeyException error) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Reference already registered");
        } catch (ResourceAccessException error) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "JIRAMA lookup unavailable");
        } catch (RestClientResponseException | JiramaLookupResponseException error) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "JIRAMA returned an unusable lookup response");
        }
    }

    public record RegistrationRequest(String msisdn, String billRef) {
    }
}