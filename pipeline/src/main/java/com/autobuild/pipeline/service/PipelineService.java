package com.autobuild.pipeline.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.mapper.PipelineMapper;
import com.autobuild.pipeline.dto.updator.PipelineUpdator;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.repository.PipelineRepository;
import com.autobuild.pipeline.utility.file.PipelineFileService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Layer for all Pipeline CRUD operations.
 * 
 * @author Suvabrata Chowdhury
 */

//TODO: Too much file reading operation is happening. Create lazy read/write logic

@Slf4j
@Service
public class PipelineService {
    @Autowired
    private PipelineRepository repository;

    @Autowired
    private PipelineMapper mapper;

    @Autowired
    private PipelineFileService fileService;

    @Autowired
    private PipelineUpdator pipelineUpdator;

    public PipelineDTO getPipelineById(final String pipelineId) throws InvalidIdException, IOException {
        emptyIdCheck(pipelineId);

        try {
            Pipeline pipeline = getPipelineFromId(pipelineId);

            PipelineDTO pipelineDTO = getPipelineDTOWithScriptContents(pipeline);

            log.info("Pipeline with id {} successfully obtained", pipelineDTO.getId());

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

        Map<UUID, String> scriptLocations = createScriptFiles(pipelineDto);
        pipeline.getStages().forEach(stage -> stage.setPath(scriptLocations.get(stage.getId())));

        Pipeline createdPipeline = repository.save(pipeline);
        PipelineDTO createdPipelineDTO = getPipelineDTOWithScriptContents(createdPipeline);

        log.info("Pipeline with id {} successfully created", createdPipeline.getId());

        return createdPipelineDTO;
    }

    public PipelineDTO updatePipeline(String pipelineId, PipelineDTO updatePipelineRequest) throws InvalidIdException, IOException {
        emptyIdCheck(pipelineId);

        try {
            Pipeline pipeline = getPipelineFromId(pipelineId);

            pipelineUpdator.update(updatePipelineRequest,pipeline);
            repository.save(pipeline);

            return getPipelineDTOWithScriptContents(pipeline);
        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("Pipeline Id is invalid");
        }
    }

    public void deletePipelineById(String pipelineId) throws IOException, InvalidIdException {
        emptyIdCheck(pipelineId);

        try {
            Pipeline pipeline = getPipelineFromId(pipelineId);

            deletePipeline(pipeline);
            log.info("Pipeline with id {} successfully deleted", pipelineId);

        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("Pipeline Id is invalid");
        }
    }

    private void emptyIdCheck(final String pipelineId) throws InvalidIdException {
        if (StringUtils.isEmpty(pipelineId)){
            throw new InvalidIdException("Pipeline Id is empty");
        }
    }

    private Pipeline getPipelineFromId(final String pipelineId) {
        Optional<Pipeline> optionalPipeline = getPipelineFromRepository(pipelineId);

        if (!optionalPipeline.isPresent()) {
            throw new EntityNotFoundException("Pipeline with id " + pipelineId + " not found");
        }
        
        Pipeline pipeline = optionalPipeline.get();
        return pipeline;
    }

    private PipelineDTO getPipelineDTOWithScriptContents(Pipeline pipeline) throws IOException {
        Map<UUID, String> stageContents = fileService.readScriptFiles(pipeline);
        
        PipelineDTO pipelineDTO = mapper.entityToDto(pipeline);
        pipelineDTO.getStages().forEach(stage -> stage.setCommand(stageContents.get(stage.getId())));
        return pipelineDTO;
    }

    private Optional<Pipeline> getPipelineFromRepository(final String pipelineId) {
        return repository.findById(UUID.fromString(pipelineId));
    }

    private Map<UUID, String> createScriptFiles(PipelineDTO pipeline) throws IOException {
        try {
            return fileService.createScriptFiles(pipeline);
        } catch (IOException e) {
            fileService.removeScriptFiles(pipeline); //to roll-back the file creation operation
            throw e;
        }
    }

    private void deletePipeline(Pipeline pipeline) throws IOException {
        deleteScriptFiles(pipeline);
        repository.delete(pipeline);
    }

    private void deleteScriptFiles(Pipeline pipeline) throws IOException {
        fileService.removeScriptFiles(pipeline);
    }
}