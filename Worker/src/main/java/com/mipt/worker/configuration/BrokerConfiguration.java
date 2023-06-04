package com.mipt.worker.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfiguration {

    public static final String QUERY_NAME = "EMAIL_TO_USER";

    @Bean
    public Queue EmailToUserQueue() {
        return new Queue(QUERY_NAME, true);
    }

}
