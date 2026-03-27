package com.lms.learning.infrastructure.client.enrollmentservicev1.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * @author Luis Balarezo
 **/
@Configuration
public class EnrollmentRestClientConfig {

    @Bean
    public RestClient enrollmentRestClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .clone()
                .baseUrl("http://enrollment-service-v1")
                .build();

    }

}
