package com.autobuild.pipeline.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.mapper.PipelineMapper;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.repository.PipelineRepository;
import com.autobuild.pipeline.utility.file.PipelineFileService;
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
    private PipelineMapper mapper;

    @Autowired
    private PipelineFileService fileService;

    // private static final String DEFAULT_SCRIPT_PATH = "./scripts"; // TODO: take this from application properties

    public PipelineDTO getPipelineById(final String pipelineId) throws InvalidIdException, IOException {
        
        if (StringUtils.isEmpty(pipelineId)){
            throw new InvalidIdException("Pipeline Id is empty");
        }

        try {
            Optional<Pipeline> optionalPipeline = repository.findById(UUID.fromString(pipelineId));

            if (!optionalPipeline.isPresent()) {
                throw new EntityNotFoundException("Pipeline with id " + pipelineId + " not found");
            }
            
            Pipeline pipeline = optionalPipeline.get();
            Map<UUID,String> stageContents = fileService.readScriptFiles(pipeline);
            
            PipelineDTO pipelineDTO = mapper.entityToDto(pipeline);
            pipelineDTO.getStages().forEach(stage -> stage.setCommand(stageContents.get(stage.getId())));

            return pipelineDTO;

        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("Pipeline Id is invalid");
        }
    }

    public PipelineDTO createPipeline(final PipelineDTO pipelineDto) throws DuplicateEntryException, IOException {
        if (null == pipelineDto) {
            return null;
        }

        //set ids of each stage and pipeline
        pipelineDto.setId(UUID.randomUUID());
        pipelineDto.getStages().forEach(stage -> stage.setId(UUID.randomUUID()));

        Pipeline pipeline = mapper.dtoToEntity(pipelineDto);

        Map<UUID,String> scriptLocations = createScriptFiles(pipelineDto);
        pipeline.getStages().forEach(stage -> stage.setPath(scriptLocations.get(stage.getId())));

        Pipeline createdPipeline = repository.save(pipeline);

        log.info("Pipeline with id {} successfully created", createdPipeline.getId());

        return mapper.entityToDto(createdPipeline);
    }

    private Map<UUID,String> createScriptFiles(PipelineDTO pipeline) throws IOException {
        try {
            return fileService.createScriptFiles(pipeline);
        } catch (IOException e) {
            fileService.removeScriptFiles(pipeline); //to roll-back the file creation operation
            throw e;
        }
    }
}