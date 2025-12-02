package com.autobuild.pipeline.executor.execution.observer;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.autobuild.pipeline.executor.entity.PipelineBuild;

/**
 * Pipeline ongoing build (or execution) observable interface.
 * 
 * @author Suvabrata Chowdhury
 */

@Component
public interface PipelineExecutionObservable {
    public void attachExecutionForObservation(PipelineBuild pipelineBuild);
    public void removeExecutionForObservation(PipelineBuild pipelineBuild);
    public List<UUID> getAllAttachedExecutions();

    //subscribe for specific build
    public void subscribe(PipelineBuild pipelineBuild, PipelineExecutionObserver subscriber);
    public void unsubscribe(PipelineBuild pipelineBuild, PipelineExecutionObserver unsubscriber);

    public List<PipelineExecutionObserver> getAllSpecificSubscribedObservers(PipelineBuild pipelineBuild);

    //subscribe for any build
    public void subscribe(PipelineExecutionObserver subscriber);
    public void unsubscribe(PipelineExecutionObserver unsubscriber);

    public List<PipelineExecutionObserver> getAllGeneralSubscribedObservers();

    // notify observers
    public void notify(PipelineBuild pipelineBuild);
}
