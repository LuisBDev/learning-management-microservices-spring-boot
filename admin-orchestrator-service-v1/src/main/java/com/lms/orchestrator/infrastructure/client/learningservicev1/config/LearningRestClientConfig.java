package com.lms.orchestrator.infrastructure.client.learningservicev1.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class LearningRestClientConfig {

    @Bean
    public RestClient learningRestClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl("http://learning-service-v1")
                .build();
    }
}
