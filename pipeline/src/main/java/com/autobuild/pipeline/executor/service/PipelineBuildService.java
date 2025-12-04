package com.autobuild.pipeline.executor.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.dto.mapper.PipelineBuildMapper;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.execution.state.PipelineExecutionState;
import com.autobuild.pipeline.executor.repository.PipelineBuildRepository;
import com.autobuild.pipeline.utility.file.PipelineFileService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for Pipeline Build.
 * 
 * @author Suvabrata Chowdhury
 */

@Slf4j
@Service
public class PipelineBuildService {

    @Autowired
    private PipelineBuildRepository repository;

    @Autowired
    private PipelineBuildMapper mapper;

    @Autowired
    private PipelineFileService pipelineFileService;

    public PipelineBuildDTO getPipelineBuild(UUID pipelineBuildId) {
        Optional<PipelineBuild> optionalPipelineBuild = repository.findById(pipelineBuildId);

        if (optionalPipelineBuild.isEmpty()) {
            EntityNotFoundException entityNotFoundException = new EntityNotFoundException(
                    "Pipeline Build with id: " + pipelineBuildId + " does not exist");
            throw entityNotFoundException;
        }

        return mapper.entityToDto(optionalPipelineBuild.get());
    }

    public List<PipelineBuildDTO> getAllBuilds() {
        List<PipelineBuild> builds = repository.findAll();
        log.info("Fetched all pipelines, count: {}", builds.size());
        return builds.stream()
                .map(pipeline -> {
                    PipelineBuildDTO dto = mapper.entityToDto(pipeline);
                    return dto;
                })
                .toList();
    }

    public void deletePipelineBuild(UUID pipelineBuildId) throws IOException {
        Optional<PipelineBuild> optionalPipelineBuild = repository.findById(pipelineBuildId);

        if (optionalPipelineBuild.isEmpty()) {
            throw new EntityNotFoundException(
                    "Pipeline Build with id: " + pipelineBuildId + " does not exist");
        }

        PipelineBuild pipelineBuild = optionalPipelineBuild.get();
        if (pipelineBuild.getCurrentState().equals(PipelineExecutionState.RUNNING)
                || pipelineBuild.getCurrentState().equals(PipelineExecutionState.WAITING)) {
            throw new IllegalStateException("Running or Waiting Pipelines can not be deleted");
        }

        pipelineFileService.removeLogFiles(pipelineBuild);
        repository.deleteById(pipelineBuildId);
    }
}
