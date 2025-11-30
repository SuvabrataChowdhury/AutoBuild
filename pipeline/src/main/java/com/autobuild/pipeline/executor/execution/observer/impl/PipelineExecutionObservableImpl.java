package com.autobuild.pipeline.executor.execution.observer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObservable;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObserver;

/**
 * Pipeline ongoing execution observer implementation.
 * 
 * @author Suvabrata Chowdhury
 */

public class PipelineExecutionObservableImpl implements PipelineExecutionObservable {

    private Map<UUID, List<PipelineExecutionObserver>> specificSubscribers = new HashMap<>();
    private List<PipelineExecutionObserver> allSubscribers = new ArrayList<>();

    // Todo: may not be required
    @Override
    public void attachExecutionForObservation(PipelineBuild pipelineBuild) {
        if (specificSubscribers.containsKey(pipelineBuild.getId())) {
            throw new IllegalArgumentException("Build is already being observed");
        }

        specificSubscribers.put(pipelineBuild.getId(), new ArrayList<>());
    }

    //todo: may not be required
    @Override
    public void removeExecutionForObservation(PipelineBuild pipelineBuild) {
        if (!specificSubscribers.containsKey(pipelineBuild.getId())) {
            throw new IllegalArgumentException("Build has not been added for removal");
        }

        specificSubscribers.remove(pipelineBuild.getId());
    }

    @Override
    public List<UUID> getAllAttachedExecutions() {
        return specificSubscribers.keySet().stream().toList();
    }

    @Override
    public void subscribe(PipelineBuild pipelineBuild, PipelineExecutionObserver subscriber) {
        if (null == subscriber || null == pipelineBuild) {
            throw new IllegalArgumentException("Null arguments given");
        }

        List<PipelineExecutionObserver> currentObserversForBuild = specificSubscribers.get(pipelineBuild.getId());

        //TODO: check if this should be allowed
        if (null == currentObserversForBuild) {
            currentObserversForBuild = new ArrayList<>();
            specificSubscribers.put(pipelineBuild.getId(), currentObserversForBuild);
        }

        currentObserversForBuild.add(subscriber);
    }

    @Override
    public void unsubscribe(PipelineBuild pipelineBuild, PipelineExecutionObserver unsubscriber) {
        if (null == unsubscriber || null == pipelineBuild) {
            throw new IllegalArgumentException("Null arguments given");
        }

        List<PipelineExecutionObserver> currentObserversForBuild = specificSubscribers.get(pipelineBuild.getId());

        if (null == currentObserversForBuild) {
            throw new IllegalArgumentException(
                    "Can not unsubscribe as no subscriber for build " + pipelineBuild.getId());
        }

        currentObserversForBuild.remove(unsubscriber);
    }

    @Override
    public List<PipelineExecutionObserver> getAllSpecificSubscribedObservers(PipelineBuild pipelineBuild) {
        return specificSubscribers.get(pipelineBuild.getId());
    }

    @Override
    public void subscribe(PipelineExecutionObserver subscriber) {
        if (null == subscriber) {
            throw new IllegalArgumentException("Null subscriber given");
        }

        allSubscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(PipelineExecutionObserver unsubscriber) {
        if (null == unsubscriber) {
            throw new IllegalArgumentException("Null unsubscriber given");
        }

        allSubscribers.remove(unsubscriber);
    }

    @Override
    public List<PipelineExecutionObserver> getAllGeneralSubscribedObservers() {
        return List.copyOf(allSubscribers);
    }

    @Override
    public void notify(PipelineBuild pipelineBuild) {
        if (null == pipelineBuild) {
            throw new IllegalArgumentException("Null pipeline build given");
        }

        List<PipelineExecutionObserver> specificObserversForPipeline = specificSubscribers.get(pipelineBuild.getId());

        if (specificObserversForPipeline != null) {
            specificObserversForPipeline.forEach(subscriber -> subscriber.update(pipelineBuild));
        }

        if (allSubscribers != null) {
            allSubscribers.forEach(subscriber -> subscriber.update(pipelineBuild));
        }
    }
}
