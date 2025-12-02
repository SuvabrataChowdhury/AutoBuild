package com.autobuild.pipeline.executor.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.dto.mapper.PipelineBuildMapper;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObservable;
import com.autobuild.pipeline.executor.execution.observer.PipelineExecutionObserver;
import com.autobuild.pipeline.executor.execution.state.PipelineExecutionState;
import com.autobuild.pipeline.executor.repository.PipelineBuildRepository;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending SSE on change of pipeline or it's stages. Implemented as
 * an Observer.
 * 
 * @author Suvabrata Chowdhury
 */

@Slf4j
@Service
public class PipelineBuildService implements PipelineExecutionObserver {

    private static final int MAX_LIVE_SUBSCRIBERS = 5;

    private final List<SseEmitter> subscriberEmitters = new CopyOnWriteArrayList<>();

    @Autowired
    private PipelineBuildRepository repository;

    @Autowired
    private PipelineExecutionObservable executionObservable;

    @Autowired
    private PipelineBuildMapper mapper;

    public void addSubscriber(SseEmitter newSubscriber, UUID pipelineBuildId) {
        Optional<PipelineBuild> optionalPipelineBuild = repository.findById(pipelineBuildId);

        if (optionalPipelineBuild.isEmpty()) {
            EntityNotFoundException entityNotFoundException = new EntityNotFoundException(
                    "Pipeline Build with id: " + pipelineBuildId + " does not exist");
            newSubscriber.completeWithError(entityNotFoundException);
            throw entityNotFoundException;
        }

        if (MAX_LIVE_SUBSCRIBERS == subscriberEmitters.size()) {
            UnsupportedOperationException exception = new UnsupportedOperationException(
                    "Maximum number of subscribers exceeded");
            newSubscriber.completeWithError(exception);
            throw exception;
        }

        if (!optionalPipelineBuild.get().getCurrentState().equals(PipelineExecutionState.RUNNING)) {
            UnsupportedOperationException exception = new UnsupportedOperationException(
                    "Live broadcast is not suppoerted as build is not in running state");
            newSubscriber.completeWithError(exception);
            throw exception;
        }

        subscriberEmitters.add(newSubscriber);

        newSubscriber.onCompletion(() -> {
            subscriberEmitters.remove(newSubscriber);
            executionObservable.unsubscribe(optionalPipelineBuild.get(), this);
        });

        newSubscriber.onError(throwable -> {
            log.error(throwable.getMessage(), throwable);
            subscriberEmitters.remove(newSubscriber);
            executionObservable.unsubscribe(optionalPipelineBuild.get(), this);
        });

        newSubscriber.onTimeout(() -> {
            subscriberEmitters.remove(newSubscriber);
            executionObservable.unsubscribe(optionalPipelineBuild.get(), this);
        });

        executionObservable.subscribe(optionalPipelineBuild.get(), this);
    }

    @Override
    public void update(PipelineBuild pipelineBuild) {
        for (SseEmitter subscriberEmitter : subscriberEmitters) {
            log.info("sending messages");
            try {
                subscriberEmitter.send(SseEmitter.event().data(mapper.entityToDto(pipelineBuild)));

                if (pipelineBuild.getCurrentState().equals(PipelineExecutionState.SUCCESS)
                        || pipelineBuild.getCurrentState().equals(PipelineExecutionState.FAILED)) {
                    subscriberEmitter.complete();
                }

            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    //TODO: Better implementation as this method is executed unconditionally
    @Scheduled(fixedRate = 15000)
    public void sentHeartBeat() {
        for (SseEmitter sseEmitter : subscriberEmitters) {
            try {
                sseEmitter.send(SseEmitter.event().comment("heartbeat pulse to keep connection open"));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @PreDestroy
    public void forceCloseAllEmitters() {
        for (SseEmitter subscriberEmitter : subscriberEmitters) {
            log.info("completing emitters");

            subscriberEmitter.complete();
        }

        subscriberEmitters.clear();
    }

    public PipelineBuildDTO getPipelineBuild(UUID pipelineBuildId) {
        Optional<PipelineBuild> optionalPipelineBuild = repository.findById(pipelineBuildId);

        if (optionalPipelineBuild.isEmpty()) {
            EntityNotFoundException entityNotFoundException = new EntityNotFoundException(
                    "Pipeline Build with id: " + pipelineBuildId + " does not exist");
            throw entityNotFoundException;
        }

        return mapper.entityToDto(optionalPipelineBuild.get());
    }
}