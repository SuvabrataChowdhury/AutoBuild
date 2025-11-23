package com.autobuild.pipeline.executor.execution.observer;

import org.springframework.stereotype.Component;

import com.autobuild.pipeline.executor.entity.PipelineBuild;

@Component
public interface PipelineExecutionObservable {
    public void attachExecutionForObservation(PipelineBuild pipelineBuild);
    public void removeExecutionForObservation(PipelineBuild pipelineBuild);

    //subscribe for specific build
    public void subscribe(PipelineBuild pipelineBuild, PipelineExecutionObserver subscriber);
    public void unsubscribe(PipelineBuild pipelineBuild, PipelineExecutionObserver unsubscriber);

    //subscribe for any build
    public void subscribe(PipelineExecutionObserver subscriber);
    public void unsubscribe(PipelineExecutionObserver unsubscriber);

    public void notify(PipelineBuild pipelineBuild);
}
