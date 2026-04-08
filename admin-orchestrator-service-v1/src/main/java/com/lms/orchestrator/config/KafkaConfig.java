package com.lms.orchestrator.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String COURSE_COMPENSATION_TOPIC = "course-service-compensation";
    public static final String ENROLLMENT_COMPENSATION_TOPIC = "enrollment-service-compensation";

    @Bean
    public NewTopic courseCompensationTopic() {
        return TopicBuilder.name(COURSE_COMPENSATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic enrollmentCompensationTopic() {
        return TopicBuilder.name(ENROLLMENT_COMPENSATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
