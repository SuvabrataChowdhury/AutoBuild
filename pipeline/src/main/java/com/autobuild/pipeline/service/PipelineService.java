package com.autobuild.pipeline.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.mapper.PipelineMapper;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.entity.Stage;
import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.repository.PipelineRepository;
import com.autobuild.pipeline.utility.file.PipelineFileService;
import com.autobuild.pipeline.utility.file.extension.Extensions;
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

    @Autowired
    private PipelineFileService fileService;

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

    public PipelineDTO createPipeline(final PipelineDTO pipelineDto) throws DuplicateEntryException, IOException {
        if (null == pipelineDto) {
            return null;
        }

        validatePipeline(pipelineDto);

        Pipeline pipeline = mapper.dtoToEntity(pipelineDto);

        // TODO: Need to remove this hard coded path setting
        // pipeline.getStages().stream()
        //         .forEach(stage -> stage.setPath(DEFAULT_SCRIPT_PATH + "/" + pipeline.getId() + "/" + stage.getId()));

        Pipeline createdPipeline = repository.save(pipeline); //TODO: Make Sure to revert changes in case of file creation errors

        createScriptFiles(createdPipeline);

        log.info("Pipeline with id {} successfully created", createdPipeline.getId());

        return mapper.entityToDto(createdPipeline);
    }

    //TODO: Too low level method. Make it more abstract
    private void createScriptFiles(Pipeline pipeline) throws IOException {
        String pipelineId = pipeline.getId().toString();
        
        try {
            fileService.createScriptFiles(pipeline);
            // for (Stage stage : pipeline.getStages()) {
            //     fileService.createScriptFiles(pipeline); //TODO: too low level call. Make it more abstract
            // }
        } catch (IOException e) {
            deleteScriptFiles(pipeline);
            throw e;
        }
    }

    private void deleteScriptFiles(Pipeline pipeline) throws IOException {
        fileService.removeScriptFiles(pipeline);
    }

    private void validatePipeline(final PipelineDTO pipelineDto) throws DuplicateEntryException {

        Errors validationErrors = validator.validatePipeline(pipelineDto);

        if (null != validationErrors && validationErrors.hasErrors()) {
            log.error("Error Occurred duplicate stages"); // TODO: better log
            throw new DuplicateEntryException(validationErrors.getAllErrors().get(0).getDefaultMessage());
        }
    }
}