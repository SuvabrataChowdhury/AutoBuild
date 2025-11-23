package com.autobuild.pipeline.executor.execution.observer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObservable;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObserver;

public class PipelineExecutionObservableImpl implements PipelineExecutionObservable{

    private Map<UUID,List<PipelineExecutionObserver>> specificSubscribers = new HashMap<>();
    private List<PipelineExecutionObserver> allSubscribers = new ArrayList<>();

    @Override
    public void attachExecutionForObservation(PipelineBuild pipelineBuild) {
        if(specificSubscribers.containsKey(pipelineBuild.getId())) {
            throw new IllegalArgumentException("Build is already being observed");
        }

        specificSubscribers.put(pipelineBuild.getId(), new ArrayList<>());
    }

    @Override
    public void removeExecutionForObservation(PipelineBuild pipelineBuild) {
        if(!specificSubscribers.containsKey(pipelineBuild.getId())) {
            throw new IllegalArgumentException("Build has not been added for removal");
        }

        specificSubscribers.remove(pipelineBuild.getId());
    }

    @Override
    public void subscribe(PipelineBuild pipelineBuild, PipelineExecutionObserver subscriber) {
        List<PipelineExecutionObserver> currentObserversForBuild = specificSubscribers.get(pipelineBuild.getId());

        if ( null == currentObserversForBuild ) {
            currentObserversForBuild = new ArrayList<>();
        }

        currentObserversForBuild.add(subscriber);
    }

    @Override
    public void unsubscribe(PipelineBuild pipelineBuild, PipelineExecutionObserver unsubscriber) {
        List<PipelineExecutionObserver> currentObserversForBuild = specificSubscribers.get(pipelineBuild.getId());

        if ( null == currentObserversForBuild ) {
            throw new IllegalArgumentException("Can not unsubscribe as no subscriber for build " + pipelineBuild.getId());
        }

        currentObserversForBuild.remove(unsubscriber);
    }

    @Override
    public void subscribe(PipelineExecutionObserver subscriber) {
        if(null == subscriber) {
            throw new IllegalArgumentException("Null subscriber given");
        }

        allSubscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(PipelineExecutionObserver unsubscriber) {
        if(null == unsubscriber) {
            throw new IllegalArgumentException("Null unsubscriber given");
        }

        allSubscribers.remove(unsubscriber);
    }

    @Override
    public void notify(PipelineBuild pipelineBuild) {
        List<PipelineExecutionObserver> specificObserversForPipeline = specificSubscribers.get(pipelineBuild.getId());

        if (specificObserversForPipeline != null) {
            specificObserversForPipeline.forEach(subscriber -> subscriber.update(pipelineBuild));
        }

        if (allSubscribers != null) {
            allSubscribers.forEach(subscriber -> subscriber.update(pipelineBuild));
        }
    }
}
