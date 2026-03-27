package com.lms.enrollment.infrastructure.client.courseservicev1.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * @author Luis Balarezo
 **/
@Configuration
public class CourseRestClientConfig {

    @Bean
    public RestClient courseRestClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .clone()
                .baseUrl("http://course-service-v1")
                .build();
    }

}
