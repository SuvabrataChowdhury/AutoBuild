package com.autobuild.pipeline.executor.execution.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PipelineExecutionState {
    WAITING("Pipeline is waiting to be executed."),
    RUNNING("Pipeline execution has started."),
    SUCCESS("Pipeline execution has finished and it succeeded."),
    FAILED("Pipeline execution has finished and it failed.");

    private String description;
}
