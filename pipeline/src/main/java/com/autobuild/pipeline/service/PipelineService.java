package com.autobuild.pipeline.service;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.mapper.PipelineMapper;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.repository.PipelineRepository;
import com.autobuild.pipeline.validator.PipelineValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Layer for all Pipeline CRUD operations.
 * @author Suvabrata Chowdhury
 */

@Slf4j
@Service
public class PipelineService {
    @Autowired
    private PipelineRepository repository;

    @Autowired
    private PipelineValidator validator;

    @Autowired
    private PipelineMapper mapper;

    public Pipeline getPipelineById(final String pipelineId) throws InvalidIdException {
        if (StringUtils.isEmpty(pipelineId))
            throw new InvalidIdException("Pipeline Id is empty");

        try {
            Optional<Pipeline> optionalPipeline = repository.findById(UUID.fromString(pipelineId));

            if (optionalPipeline.isPresent()) {
                return optionalPipeline.get();
            }

            log.info("Pipeline with id " + pipelineId + " not found");

            return null;

        } catch (IllegalArgumentException e) {
            // log.debug("Exception occured", e); //TODO: check correct places for debug logs
            log.error("Pipeline Id is invalid");

            throw new InvalidIdException("Pipeline Id is invalid");
        }
    }

    public Pipeline createPipeline(final PipelineDTO pipelineDto) throws DuplicateEntryException {
        validatePipeline(pipelineDto);

        // createPipelineStageScripts(pipeline);

        Pipeline pipeline = mapper.dtoToEntity(pipelineDto);

        try {
            return repository.save(pipeline);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException("Pipeline with the name \"" + pipeline.getName() + "\" already exists");
        }
    }

    // private void createPipelineStageScripts(Pipeline pipeline) {
    //     for(BashStageImpl stage: pipeline.getStages()) {

    //     }
    // }

    //TODO: Use a better validation class implementation
    private void validatePipeline(final PipelineDTO pipelineDto) throws DuplicateEntryException {
        // Errors validationErrors = new BeanPropertyBindingResult(pipeline, "pipeline");
        // validator.validate(pipeline, validationErrors);

        Errors validationErrors = validator.validatePipeline(pipelineDto);

        if (null != validationErrors && validationErrors.hasErrors()) {
            log.error("Error Occurred duplicate stages"); //TODO: better log
            throw new DuplicateEntryException(validationErrors.getAllErrors().get(0).getDefaultMessage());
        }
    }
}