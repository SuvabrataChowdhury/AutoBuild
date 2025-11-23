package com.autobuild.pipeline.executor.execution.observer;

import com.autobuild.pipeline.executor.entity.PipelineBuild;

public interface PipelineExecutionObserver {
    public void update(PipelineBuild pipelineBuild);
}