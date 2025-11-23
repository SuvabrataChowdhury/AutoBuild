package com.autobuild.pipeline.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.autobuild.pipeline.executor.job.PipelineExecutor;
import com.autobuild.pipeline.executor.job.PipelineExecutorImpl;

@Configuration
@EnableAsync
public class ExecutorConfig {
    private static final int MAX_THREAD_COUNT = 5;

    @Bean(name = "executorService")
    public ThreadPoolTaskExecutor getExecutorService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(MAX_THREAD_COUNT);
        executor.setMaxPoolSize(MAX_THREAD_COUNT);
        executor.setQueueCapacity(Integer.MAX_VALUE); // like Executors.newFixedThreadPool
        executor.setThreadNamePrefix("executorService-");

        executor.initialize();
        return executor;
    }

    @Bean
    public PipelineExecutor getPipelineExecutor() {
        return new PipelineExecutorImpl();
    }
}
