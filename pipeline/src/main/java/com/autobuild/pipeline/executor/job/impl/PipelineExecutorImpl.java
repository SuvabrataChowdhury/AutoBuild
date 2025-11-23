package com.autobuild.pipeline.executor.job.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.entity.StageBuild;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObservable;
import com.autobuild.pipeline.executor.execution.state.PipelineExecutionState;
import com.autobuild.pipeline.executor.execution.state.StageExecutionState;
import com.autobuild.pipeline.executor.job.PipelineExecutor;

import lombok.extern.slf4j.Slf4j;

//TODO: Ugly design for now.
@Slf4j
public class PipelineExecutorImpl implements PipelineExecutor{

    @Autowired
    private PipelineExecutionObservable pipelineExecutionObservable;

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

        boolean anyStageBuildFailed = false;

        for(final StageBuild stageBuild : pipelineBuild.getStageBuilds()) {

            if (anyStageBuildFailed) {
                stageBuild.setCurrentState(StageExecutionState.STOPPED);
                pipelineExecutionObservable.notify(pipelineBuild);

                continue;
            }

            ProcessBuilder stageProcessBuilder = new ProcessBuilder(stageBuild.getStage().getPath());
            Process stageProcess = stageProcessBuilder.start();

            stageBuild.setCurrentState(StageExecutionState.RUNNING);
            pipelineExecutionObservable.notify(pipelineBuild);

            log.info("process started with id: " + stageProcess.pid());

            //TODO: attach observer which will be used to live broadcast 
            int processExitCode = stageProcess.waitFor();
            if (processExitCode != 0) {
                anyStageBuildFailed = true;
                stageBuild.setCurrentState(StageExecutionState.FAILED);

                log.error("process exited with id: " + stageProcess.pid());
                pipelineBuild.setCurrentState(PipelineExecutionState.FAILED);
                continue;
            }

            stageBuild.setCurrentState(StageExecutionState.SUCCESS);
            pipelineExecutionObservable.notify(pipelineBuild);
        }

        if (pipelineBuild.getCurrentState() != PipelineExecutionState.FAILED) {
            pipelineBuild.setCurrentState(PipelineExecutionState.SUCCESS);
        }

        pipelineExecutionObservable.notify(pipelineBuild);
    }
}