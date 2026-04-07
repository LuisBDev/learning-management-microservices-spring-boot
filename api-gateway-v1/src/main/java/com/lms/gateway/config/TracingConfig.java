package com.lms.gateway.config;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class TracingConfig {

    @Bean
    public GlobalFilter traceResponseFilter(Tracer tracer) {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    var currentSpan = tracer.currentSpan();
                    if (currentSpan != null) {
                        String traceId = currentSpan.context().traceId();
                        exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
                    }
                }));
    }

}
