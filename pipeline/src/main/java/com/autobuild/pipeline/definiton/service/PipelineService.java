package com.autobuild.pipeline.definiton.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
 * @author Suvabrata Chowdhury && Baibhab Dey
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

    // REMOVED PipelineValidator

    public PipelineDTO getPipelineById(final String pipelineId) throws InvalidIdException, IOException {
        validatePipelineId(pipelineId);
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

    public PipelineDTO createPipeline(final PipelineDTO pipelineDto)
            throws IOException, DuplicateEntryException, InvalidIdException {
        if (pipelineDto == null) {
            return null;
        }
        if (pipelineDto.getName() != null && repository.existsByName(pipelineDto.getName())) {
            throw new DuplicateEntryException("Pipeline with name '" + pipelineDto.getName() + "' already exists");
        }

        // Enforce only bash script type for all incoming stages.
        if (pipelineDto.getStages() != null) {
            for (StageDTO s : pipelineDto.getStages()) {
                validateScriptTypeOnCreate(s.getScriptType());
            }
        }

        pipelineDto.setId(UUID.randomUUID());
        if (pipelineDto.getStages() != null) {
            pipelineDto.getStages().forEach(stage -> stage.setId(UUID.randomUUID()));
        }
        Pipeline pipeline = mapper.dtoToEntity(pipelineDto);
        Map<UUID, String> scriptLocations = createScriptFiles(pipelineDto);
        pipeline.getStages().forEach(stage -> stage.setPath(scriptLocations.get(stage.getId())));
        Pipeline createdPipeline = repository.save(pipeline);
        return mapper.entityToDto(createdPipeline);
    }

    public void deletePipelineById(String pipelineId) throws IOException, InvalidIdException {
        validatePipelineId(pipelineId);
        try {
            Pipeline pipeline = getPipelineFromId(pipelineId);
            deletePipeline(pipeline);
            log.info("Pipeline with id {} successfully deleted", pipelineId);
        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("Pipeline Id is invalid");
        }
    }

    public PipelineDTO modifyPipeline(String pipelineId, PipelineDTO patchRequest)
            throws InvalidIdException, IOException, DuplicateEntryException {

        validatePipelineId(pipelineId);
        validateModifyRequest(patchRequest);

        Pipeline pipeline = getPipelineFromId(pipelineId);

        boolean nameChange = patchRequest.getName() != null && !patchRequest.getName().isBlank();
        boolean stagesChange = patchRequest.getStages() != null && !patchRequest.getStages().isEmpty();

        if (nameChange) {
            // Optional pre-check; if you prefer DB constraint remove this block.
            if (!patchRequest.getName().equals(pipeline.getName()) &&
                    repository.existsByName(patchRequest.getName())) {
                throw new DuplicateEntryException("Pipeline with name '" + patchRequest.getName() + "' already exists");
            }
            pipeline.setName(patchRequest.getName());
        }

        if (stagesChange) {
            processStageChanges(pipeline, patchRequest.getStages());
        }

        repository.save(pipeline);

        Map<UUID, String> finalScripts = fileService.readScriptFiles(pipeline);
        PipelineDTO response = mapper.entityToDto(pipeline);
        response.getStages().forEach(s -> s.setCommand(finalScripts.get(s.getId())));
        log.info("Pipeline with id {} successfully modified", pipelineId);
        return response;
    }

    private void validatePipelineId(String pipelineId) throws InvalidIdException {
        if (pipelineId == null || pipelineId.isBlank()) {
            throw new InvalidIdException("Pipeline Id is empty");
        }
    }

    private void validateModifyRequest(PipelineDTO request) throws InvalidIdException {
        boolean hasName = request.getName() != null && !request.getName().isBlank();
        boolean hasStages = request.getStages() != null && !request.getStages().isEmpty();
        if (!hasName && !hasStages) {
            throw new InvalidIdException("Request must contain either name or stages");
        }
    }

    private void processStageChanges(Pipeline pipeline, List<StageDTO> stageRequests)
            throws InvalidIdException, IOException, DuplicateEntryException {

        validateStageRequests(stageRequests);

        for (StageDTO req : stageRequests) {
            boolean isCreate = req.getId() == null;
            boolean isDelete = !isCreate
                    && req.getName() == null
                    && req.getScriptType() == null
                    && req.getCommand() == null;

            if (isCreate) {
                createStage(pipeline, req);
                continue;
            }
            if (isDelete) {
                deleteStage(pipeline, req.getId());
                continue;
            }
            updateStage(pipeline, req);
        }
    }

    private void createStage(Pipeline pipeline, StageDTO req)
            throws IOException, InvalidIdException {
        validateScriptTypeOnCreate(req.getScriptType());
        req.setId(UUID.randomUUID());
        Stage entity = stageMapper.dtoToEntity(req);
        String path = fileService.createStageScriptFile(pipeline, req);
        entity.setPath(path);
        pipeline.getStages().add(entity);
    }
    private void updateStage(Pipeline pipeline, StageDTO req)
            throws InvalidIdException, IOException {

        Stage toUpdate = findStage(pipeline, req.getId());

        if (!isBlank(req.getName())) {
            toUpdate.setName(req.getName());
        }

        // Any attempt to provide scriptType in update request is forbidden.
        if (req.getScriptType() != null) {
            throw new InvalidIdException("Script type updates are not allowed");
        }

        if (!isBlank(req.getCommand())) {
            fileService.updateStageScriptFile(toUpdate, req.getCommand());
        }
    }

    private void validateStageRequests(List<StageDTO> requests)
            throws InvalidIdException, DuplicateEntryException {

        Set<UUID> ids = new HashSet<>();

        for (StageDTO req : requests) {
            boolean isCreate = req.getId() == null;
            boolean isDelete = !isCreate
                    && req.getName() == null
                    && req.getScriptType() == null
                    && req.getCommand() == null;

            if (isCreate) {
                if (isBlank(req.getName()) || isBlank(req.getScriptType()) || isBlank(req.getCommand())) {
                    throw new InvalidIdException("Create stage requires name, scriptType, and command");
                }
                validateScriptTypeOnCreate(req.getScriptType());
                continue;
            }

            if (isDelete) {
                continue;
            }

            // For update: scriptType must not be present.
            if (req.getScriptType() != null) {
                throw new InvalidIdException("Script type updates are not allowed");
            }

            if (!ids.add(req.getId())) {
                throw new DuplicateEntryException("Duplicate stage ID in request: " + req.getId());
            }
        }
    }

    private void validateScriptTypeOnCreate(String scriptType) throws InvalidIdException {
        if (scriptType == null || !scriptType.equalsIgnoreCase("bash")) {
            throw new InvalidIdException("Unsupported script type: " + scriptType + ". Only 'bash' is allowed");
        }
    }
    private void deleteStage(Pipeline pipeline, UUID stageId)
            throws InvalidIdException, IOException {
        Stage toDelete = findStage(pipeline, stageId);
        fileService.removeStageScriptFile(toDelete);
        pipeline.getStages().removeIf(s -> s.getId().equals(stageId));
    }

    private Stage findStage(Pipeline pipeline, UUID id) throws InvalidIdException {
        return pipeline.getStages().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new InvalidIdException("Stage not found: " + id));
    }

    private Pipeline getPipelineFromId(final String pipelineId) {
        Optional<Pipeline> optionalPipeline = repository.findById(UUID.fromString(pipelineId));
        if (!optionalPipeline.isPresent()) {
            throw new EntityNotFoundException("Pipeline with id " + pipelineId + " not found");
        }
        return optionalPipeline.get();
    }

    private Map<UUID, String> createScriptFiles(PipelineDTO pipeline) throws IOException {
        try {
            return fileService.createScriptFiles(pipeline);
        } catch (IOException e) {
            fileService.removeScriptFiles(pipeline);
            throw e;
        }
    }

    private void deletePipeline(Pipeline pipeline) throws IOException {
        fileService.removeScriptFiles(mapper.entityToDto(pipeline));
        repository.delete(pipeline);
    }

    private boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}