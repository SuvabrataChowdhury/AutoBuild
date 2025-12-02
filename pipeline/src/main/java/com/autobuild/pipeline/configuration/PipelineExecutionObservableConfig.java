package com.autobuild.pipeline.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObservable;
import com.autobuild.pipeline.executor.execution.observer.impl.PipelineExecutionObservableImpl;

/**
 * Pipeline Observable config for observer pattern.
 * 
 * @author Suvabrata Chowdhury
 */

@Configuration
public class PipelineExecutionObservableConfig {
    @Bean
    public PipelineExecutionObservable getPipelineExecutionObservable() {
        return new PipelineExecutionObservableImpl();
    }
}
