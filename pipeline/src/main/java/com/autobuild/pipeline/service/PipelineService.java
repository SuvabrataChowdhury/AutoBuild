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

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Layer for all Pipeline CRUD operations.
 * 
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

    private static final String DEFAULT_SCRIPT_PATH = "./scripts"; // TODO: take this from application properties

    public PipelineDTO getPipelineById(final String pipelineId) throws InvalidIdException {
        if (StringUtils.isEmpty(pipelineId))
            throw new InvalidIdException("Pipeline Id is empty");

        try {
            Optional<Pipeline> optionalPipeline = repository.findById(UUID.fromString(pipelineId));

            if (!optionalPipeline.isPresent()) {
                throw new EntityNotFoundException("Pipeline with id " + pipelineId + " not found");
            }

            return mapper.entityToDto(optionalPipeline.get());

        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("Pipeline Id is invalid");
        }
    }

    public PipelineDTO createPipeline(final PipelineDTO pipelineDto) throws DuplicateEntryException {
        validatePipeline(pipelineDto);

        Pipeline pipeline = mapper.dtoToEntity(pipelineDto);

        // TODO: Need to remove this hard coded path setting
        pipeline.getStages().stream()
                .forEach(stage -> stage.setPath(DEFAULT_SCRIPT_PATH + "/" + pipeline.getId() + "/" + stage.getId()));

        Pipeline createdPipeline = repository.save(pipeline);
        log.info("Pipeline with id {} successfully created", createdPipeline.getId());

        return mapper.entityToDto(createdPipeline);

    }

    private void validatePipeline(final PipelineDTO pipelineDto) throws DuplicateEntryException {

        Errors validationErrors = validator.validatePipeline(pipelineDto);

        if (null != validationErrors && validationErrors.hasErrors()) {
            log.error("Error Occurred duplicate stages"); // TODO: better log
            throw new DuplicateEntryException(validationErrors.getAllErrors().get(0).getDefaultMessage());
        }
    }
}