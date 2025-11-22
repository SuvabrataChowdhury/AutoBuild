package com.autobuild.pipeline.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {
    private static final int MAX_THREAD_COUNT = 5;

    @Bean
    public ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    }
    
}
