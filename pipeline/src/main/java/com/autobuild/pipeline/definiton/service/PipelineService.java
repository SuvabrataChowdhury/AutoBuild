package com.autobuild.pipeline.definiton.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobuild.pipeline.definiton.dto.PipelineDTO;
import com.autobuild.pipeline.definiton.dto.mapper.PipelineMapper;
import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.definiton.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.definiton.exceptions.InvalidIdException;
import com.autobuild.pipeline.definiton.repository.PipelineRepository;
import com.autobuild.pipeline.utility.file.PipelineFileService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import com.autobuild.pipeline.definiton.dto.StageDTO;
import com.autobuild.pipeline.definiton.dto.mapper.StageMapper;
import com.autobuild.pipeline.definiton.entity.Stage;

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

    @Autowired
    private StageMapper stageMapper;

    public PipelineDTO getPipelineById(final String pipelineId) throws InvalidIdException, IOException {
        emptyIdCheck(pipelineId);

        try {
            Pipeline pipeline = getPipelineFromId(pipelineId);
            Map<UUID, String> stageContents = fileService.readScriptFiles(pipeline);

            PipelineDTO pipelineDTO = mapper.entityToDto(pipeline);
            pipelineDTO.getStages().forEach(stage -> stage.setCommand(stageContents.get(stage.getId())));

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

        // set ids of each stage and pipeline
        pipelineDto.setId(UUID.randomUUID());
        pipelineDto.getStages().forEach(stage -> stage.setId(UUID.randomUUID()));

        Pipeline pipeline = mapper.dtoToEntity(pipelineDto);

        Map<UUID, String> scriptLocations = createScriptFiles(pipelineDto);
        pipeline.getStages().forEach(stage -> stage.setPath(scriptLocations.get(stage.getId())));

        Pipeline createdPipeline = repository.save(pipeline);

        log.info("Pipeline with id {} successfully created", createdPipeline.getId());

        return mapper.entityToDto(createdPipeline);
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
        if (StringUtils.isEmpty(pipelineId)) {
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

    private Optional<Pipeline> getPipelineFromRepository(final String pipelineId) {
        return repository.findById(UUID.fromString(pipelineId));
    }

    private Map<UUID, String> createScriptFiles(PipelineDTO pipeline) throws IOException {
        try {
            return fileService.createScriptFiles(pipeline);
        } catch (IOException e) {
            fileService.removeScriptFiles(pipeline); // to roll-back the file creation operation
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

    public PipelineDTO modifyPipelineStages(String pipelineId, List<StageDTO> stageRequests)
            throws InvalidIdException, IOException, DuplicateEntryException {

        emptyIdCheck(pipelineId);
        if (stageRequests == null || stageRequests.isEmpty()) {
            throw new InvalidIdException("No stages provided");
        }

        Pipeline pipeline = getPipelineFromId(pipelineId);

        Set<String> existingNames = new HashSet<>();
        pipeline.getStages().forEach(s -> existingNames.add(s.getName().toLowerCase()));

        Set<String> newNamesThisCall = new HashSet<>();

        for (StageDTO req : stageRequests) {
            boolean isCreate = (req.getId() == null);
            boolean isDelete = !isCreate &&
                    isBlank(req.getName())
                    && isBlank(req.getScriptType())
                    && isBlank(req.getCommand());

            if (isCreate) {
                createStage(pipeline, req, existingNames, newNamesThisCall);
            } else if (isDelete) {
                deleteStage(pipeline, req.getId(), existingNames);
            } else {
                updateStage(pipeline, req, existingNames, newNamesThisCall);
            }
        }

        repository.save(pipeline);

        Map<UUID, String> finalScripts = fileService.readScriptFiles(pipeline);
        PipelineDTO response = mapper.entityToDto(pipeline);
        response.getStages().forEach(s -> s.setCommand(finalScripts.get(s.getId())));
        return response;
    }

    private void createStage(Pipeline pipeline,
            StageDTO req, Set<String> existingNames,
            Set<String> newNamesThisCall)
            throws InvalidIdException, DuplicateEntryException, IOException {

        validateCreateStage(req);
        String lower = req.getName().toLowerCase();
        if (existingNames.contains(lower) || newNamesThisCall.contains(lower)) {
            throw new DuplicateEntryException("Duplicate stage name: " + req.getName());
        }
        req.setId(UUID.randomUUID());
        Stage entity = stageMapper.dtoToEntity(req);
        String path = fileService.createStageScriptFile(pipeline, req);
        entity.setPath(path);
        pipeline.getStages().add(entity);
        newNamesThisCall.add(lower);
        existingNames.add(lower);
    }

    private void updateStage(Pipeline pipeline, StageDTO req,
            Set<String> existingNames, Set<String> newNamesThisCall)
            throws InvalidIdException, DuplicateEntryException, IOException {

        Stage toUpdate = findStage(pipeline, req.getId());

        if (!isBlank(req.getName())) {
            String newLower = req.getName().toLowerCase();
            String oldLower = toUpdate.getName().toLowerCase();
            if (!newLower.equals(oldLower)
                    && (existingNames.contains(newLower) || newNamesThisCall.contains(newLower))) {
                throw new DuplicateEntryException("Duplicate stage name on update: " + req.getName());
            }
            existingNames.remove(oldLower);
            toUpdate.setName(req.getName());
            existingNames.add(newLower);
            newNamesThisCall.add(newLower);
        }

        if (!isBlank(req.getScriptType())) {
            toUpdate.setScriptType(req.getScriptType());
        }

        if (!isBlank(req.getCommand())) {
            fileService.updateStageScriptFile(toUpdate, req.getCommand());
        }
    }

    private void deleteStage(Pipeline pipeline, UUID stageId,
            Set<String> existingNames)
            throws InvalidIdException, IOException {

        Stage toDelete = findStage(pipeline, stageId);
        fileService.removeStageScriptFile(toDelete);
        pipeline.getStages().removeIf(s -> s.getId().equals(stageId));
        existingNames.remove(toDelete.getName().toLowerCase());
    }

    private Stage findStage(Pipeline pipeline, UUID id)
            throws InvalidIdException {
        return pipeline.getStages().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new InvalidIdException("Stage not found: " + id));
    }

    private void validateCreateStage(StageDTO req) throws InvalidIdException {
        if (isBlank(req.getName()) || isBlank(req.getScriptType()) || isBlank(req.getCommand())) {
            throw new InvalidIdException("Create stage requires name, scriptType, command");
        }
    }
    public PipelineDTO updatePipelineName(String pipelineId, String newName) throws InvalidIdException {
        emptyIdCheck(pipelineId);
    
        if (StringUtils.isBlank(newName)) {
            throw new InvalidIdException("Pipeline name cannot be blank");
        }
    
        Pipeline pipeline = getPipelineFromId(pipelineId);
        pipeline.setName(newName);
    
        Pipeline updatedPipeline = repository.save(pipeline);
        log.info("Pipeline with id {} successfully updated with new name {}", pipelineId, newName);
    
        return mapper.entityToDto(updatedPipeline);
    }

    private boolean isBlank(String v) {
        return StringUtils.isBlank(v);
    }
}