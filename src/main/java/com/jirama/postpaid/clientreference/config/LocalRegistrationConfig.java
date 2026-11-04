package com.jirama.postpaid.clientreference.config;

import com.jirama.postpaid.clientreference.partner.JiramaPostpaidClient;
import com.jirama.postpaid.clientreference.repository.ClientReferenceRepository;
import com.jirama.postpaid.clientreference.service.ClientReferenceRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("local-registration")
public class LocalRegistrationConfig {

    @Bean
    JiramaPostpaidClient jiramaPostpaidClient() {
        return JiramaPostpaidClient.withTimeouts("http://127.0.0.1:8090");
    }

    @Bean
    ClientReferenceRegistration registration(
        JiramaPostpaidClient partnerClient,
        ClientReferenceRepository repository
    ) {
        return new ClientReferenceRegistration(partnerClient, repository);
    }
}