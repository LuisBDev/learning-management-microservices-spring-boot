package com.lms.course.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    ServletRequestAttributes attrs =
                            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs != null) {
                        HttpServletRequest servletRequest = attrs.getRequest();
                        String authHeader = servletRequest.getHeader("Authorization");
                        if (authHeader != null) {
                            request.getHeaders().add("Authorization", authHeader);
                        }
                    }
                    return execution.execute(request, body);
                });
    }

    //@Primary para que esta sea la que se inyecte por defecto al cliente de Eureka
    @Primary
    @Bean
    public RestClient.Builder cleanRestClientBuilder() {
        return RestClient.builder();
    }

}
