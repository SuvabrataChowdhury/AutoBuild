package com.autobuild.pipeline.executor.execution.observer;

import com.autobuild.pipeline.executor.entity.PipelineBuild;

/**
 * Pipeline ongoing build (or execution) observer interface.
 * 
 * @author Suvabrata Chowdhury
 */
public interface PipelineExecutionObserver {
    public void update(PipelineBuild pipelineBuild);
}