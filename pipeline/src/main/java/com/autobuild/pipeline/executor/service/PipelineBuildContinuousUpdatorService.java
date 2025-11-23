package com.autobuild.pipeline.executor.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObservable;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObserver;
import com.autobuild.pipeline.executor.repository.PipelineBuildRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PipelineBuildContinuousUpdatorService implements PipelineExecutionObserver{

    @Autowired
    private PipelineBuildRepository repository;

    private PipelineExecutionObservable pipelineExecutionObservable;

    @Autowired
    public PipelineBuildContinuousUpdatorService(PipelineExecutionObservable pipelineExecutionObservable) {
        this.pipelineExecutionObservable = pipelineExecutionObservable;
        this.pipelineExecutionObservable.subscribe(this);
    }

    public PipelineBuildContinuousUpdatorService() {
        pipelineExecutionObservable.subscribe(this);
    }

    @Transactional
    @Override
    public void update(PipelineBuild pipelineBuild) {
        log.info("Updating build with state: " + pipelineBuild.getCurrentState());
        
        Optional<PipelineBuild> optionalBuild = repository.findById(pipelineBuild.getId());

        log.info("Found optional");

        if(optionalBuild.isEmpty()) {
            throw new EntityNotFoundException("Pipeline build with id " + pipelineBuild.getId() + " does not exist");
        }

        log.info("saving build's status");
        repository.save(pipelineBuild);

        log.info("Updated build: " + pipelineBuild.getCurrentState());
    }

}
