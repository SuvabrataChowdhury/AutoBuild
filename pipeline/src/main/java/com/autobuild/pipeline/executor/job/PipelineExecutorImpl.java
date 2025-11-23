package com.autobuild.pipeline.executor.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.entity.StageBuild;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObservable;
import com.autobuild.pipeline.executor.execution.state.PipelineExecutionState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipelineExecutorImpl implements PipelineExecutor{

    // @Autowired
    // private ExecutorService executorService;

    @Autowired
    private PipelineExecutionObservable pipelineExecutionObservable;

    // @Autowired
    // private 

    @Async("executorService")
    @Override
    public void executePipeline(PipelineBuild pipelineBuild) {
        try {
            log.info("inside executor impl submit");
            pipelineBuild.setCurrentState(PipelineExecutionState.WAITING);
            pipelineExecutionObservable.attachExecutionForObservation(pipelineBuild);

            this.startBuild(pipelineBuild);

            pipelineExecutionObservable.removeExecutionForObservation(pipelineBuild);
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //TODO: abstract this logic
    private void startBuild(PipelineBuild pipelineBuild) throws IOException, InterruptedException {
        Pipeline pipeline = pipelineBuild.getPipeline();
        log.info("Starting Build for: " + pipeline.getId());

        //TODO: with state manager. Finite State Machine
        pipelineBuild.setCurrentState(PipelineExecutionState.RUNNING);
        pipelineExecutionObservable.notify(pipelineBuild);

        for(final StageBuild stageBuild : pipelineBuild.getStageBuilds()) {
            ProcessBuilder stageProcessBuilder = new ProcessBuilder(stageBuild.getStage().getPath());
            Process stageProcess = stageProcessBuilder.start();
            log.info("process started with id: " + stageProcess.pid());

            //TODO: attach observer which will be used to live broadcast 
            if(stageProcess.waitFor() != 0) {
                log.info("process started with id: " + stageProcess.pid());
                pipelineBuild.setCurrentState(PipelineExecutionState.FAILED);
                break;
            }
        }

        if (pipelineBuild.getCurrentState() != PipelineExecutionState.FAILED) {
            pipelineBuild.setCurrentState(PipelineExecutionState.SUCCESS);
        }

        pipelineExecutionObservable.notify(pipelineBuild);
    }
}
