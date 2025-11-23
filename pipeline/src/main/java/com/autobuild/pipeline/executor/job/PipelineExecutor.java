package com.autobuild.pipeline.executor.job;

import org.springframework.stereotype.Component;

import com.autobuild.pipeline.executor.entity.PipelineBuild;

@Component
public interface PipelineExecutor {
    public void executePipeline(PipelineBuild pipelineBuild);
}
