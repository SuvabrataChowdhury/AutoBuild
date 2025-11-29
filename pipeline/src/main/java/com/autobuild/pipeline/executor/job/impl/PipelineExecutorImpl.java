package com.autobuild.pipeline.executor.job.impl;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.entity.StageBuild;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObservable;
import com.autobuild.pipeline.executor.execution.state.PipelineExecutionState;
import com.autobuild.pipeline.executor.execution.state.StageExecutionState;
import com.autobuild.pipeline.executor.job.PipelineExecutor;
import com.autobuild.pipeline.utility.file.PipelineFileService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation for asynchronously start executing pipeline build.
 * 
 * @author Suvabrata Chowdhury
 */

@Slf4j
public class PipelineExecutorImpl implements PipelineExecutor {

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

    private void startBuild(PipelineBuild pipelineBuild) throws IOException, InterruptedException {
        Pipeline pipeline = pipelineBuild.getPipeline();
        log.info("Starting Build for: " + pipeline.getId());

        startPipelineStageBuilds(pipelineBuild);
    }

    // TODO: Ugly design for now. Refactor later.
    private void startPipelineStageBuilds(PipelineBuild pipelineBuild) throws IOException, InterruptedException {

        // TODO: with state manager. Finite State Machine
        setPipelineBuildState(pipelineBuild, PipelineExecutionState.RUNNING);

        boolean anyStageBuildFailed = false;

        for (final StageBuild stageBuild : pipelineBuild.getStageBuilds()) {

            if (anyStageBuildFailed) {
                stageBuild.setCurrentState(StageExecutionState.STOPPED);

                continue;
            }

            // String logPath = pipelineFileService.createLogFile(pipelineBuild.getId(), stageBuild.getId());
            Process stageProcess = startStageProcess(stageBuild);

            setStageBuildState(pipelineBuild, stageBuild, StageExecutionState.RUNNING);

            log.info("process started with id: " + stageProcess.pid());

            int processExitCode = stageProcess.waitFor();
            if (processExitCode != 0) {
                anyStageBuildFailed = true;
                stageBuild.setCurrentState(StageExecutionState.FAILED);

                log.error("process exited with id: " + stageProcess.pid());
                continue;
            }

            setStageBuildState(pipelineBuild, stageBuild, StageExecutionState.SUCCESS);
        }

        if (anyStageBuildFailed) {
            pipelineBuild.setCurrentState(PipelineExecutionState.FAILED);
        } else {
            pipelineBuild.setCurrentState(PipelineExecutionState.SUCCESS);
        }

        pipelineExecutionObservable.notify(pipelineBuild);
    }

    private Process startStageProcess(final StageBuild stageBuild) throws IOException {
        ProcessBuilder stageProcessBuilder = new ProcessBuilder(stageBuild.getStage().getPath());

        stageProcessBuilder.redirectOutput(Redirect.to(Path.of(stageBuild.getLogPath()).toFile()));
        stageProcessBuilder.redirectErrorStream(true);

        Process stageProcess = stageProcessBuilder.start();
        return stageProcess;
    }

    private void setStageBuildState(PipelineBuild pipelineBuild, final StageBuild stageBuild,
            final StageExecutionState stageExecutionState) {
        stageBuild.setCurrentState(stageExecutionState);
        pipelineExecutionObservable.notify(pipelineBuild);
    }

    private void setPipelineBuildState(PipelineBuild pipelineBuild, PipelineExecutionState pipelineExecutionState) {
        pipelineBuild.setCurrentState(pipelineExecutionState);
        pipelineExecutionObservable.notify(pipelineBuild);
    }
}