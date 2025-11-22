package com.autobuild.pipeline.executor.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.executor.entity.PipelineBuild;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PipelineExecutor {
    // @Value("${}")
    // private static final int MAX_THREAD_COUNT = 5;
    // private final ExecutorService jobExecutorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

    @Autowired
    private ExecutorService executorService;

    public void execute(PipelineBuild pipelineBuild) {

        executorService.submit(() -> this.startBuild(pipelineBuild));
    }

    //TODO: abstract this logic
    private void startBuild(PipelineBuild pipelineBuild) {
        Pipeline pipeline = pipelineBuild.getPipeline();
        log.info("Starting Build for: " + pipeline.getId());

        try {
            pipeline.getStages().forEach(stage -> {
                ProcessBuilder stageProcessBuilder = new ProcessBuilder(stage.getPath());
                try {
                    Process stageProcess = stageProcessBuilder.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stageProcess.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            log.info("OP: " + line);
                        }                        
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }catch (UncheckedIOException exception) {
            exception.getCause().printStackTrace();
        }
    }
}
