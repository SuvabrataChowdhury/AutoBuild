package com.autobuild.pipeline.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobuild.pipeline.dto.Pipeline;
import com.autobuild.pipeline.repository.PipelineRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PipelineService {
    @Autowired
    private PipelineRepository repository;

    public Pipeline createPipeline(final Pipeline pipeline) {
        return repository.save(pipeline);
    }

    public Pipeline getPipelineById(final String pipelineId) {
        try {
            Optional<Pipeline> optionalPipeline = repository.findById(UUID.fromString(pipelineId));

            return (optionalPipeline.isPresent()) ? optionalPipeline.get() : null;
        } catch (IllegalArgumentException e) {

            log.debug("Exception occured", e);
            log.error("UUID is invalid");

            return null;
        }
    }
}