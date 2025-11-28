package com.autobuild.pipeline.executor.execution.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Stage ongoing build (execution) stages.
 * 
 * @author Suvabrata Chowdhury
 */

@AllArgsConstructor
@Getter
public enum StageExecutionState {
    WAITING("Stage is waiting to be executed."),
    RUNNING("Stage execution has started."),
    SUCCESS("Stage execution has finished and it succeeded."),
    FAILED("Stage execution has finished and it failed."),
    STOPPED("Stage execution stopped");

    private String description;
}
