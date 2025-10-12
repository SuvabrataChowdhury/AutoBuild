package com.autobuild.pipeline.service;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.repository.PipelineRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PipelineService {
    @Autowired
    private PipelineRepository repository;

    public Pipeline getPipelineById(final String pipelineId) throws InvalidIdException {
        if(StringUtils.isEmpty(pipelineId))
            throw new InvalidIdException("Pipeline Id is empty");

        try {
            Optional<Pipeline> optionalPipeline = repository.findById(UUID.fromString(pipelineId));

            return (optionalPipeline.isPresent()) ? optionalPipeline.get() : null;
        } catch (IllegalArgumentException e) {
            // log.debug("Exception occured", e); //TODO: check correct places for debug logs
            log.error("Pipeline Id is invalid");

            throw new InvalidIdException("Pipeline Id is invalid");
        }
    }

    public Pipeline createPipeline(final Pipeline pipeline) {
        return repository.save(pipeline);
    }
}