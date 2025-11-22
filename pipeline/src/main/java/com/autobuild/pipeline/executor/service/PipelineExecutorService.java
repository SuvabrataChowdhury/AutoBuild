package com.autobuild.pipeline.executor.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.definiton.repository.PipelineRepository;
import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.dto.PipelineExecuteRequest;
import com.autobuild.pipeline.executor.dto.mapper.PipelineBuildMapper;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.entity.StageBuild;
import com.autobuild.pipeline.executor.job.PipelineExecutor;
import com.autobuild.pipeline.executor.repository.PipelineBuildRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for executing a pipeline.
 * 
 * @author Suvabrata Chowdhury
 */

@Slf4j
@Service
public class PipelineExecutorService {

    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private PipelineBuildRepository buildRepository;

    @Autowired
    private PipelineExecutor pipelineExecutor;

    @Autowired
    private PipelineBuildMapper mapper;

    @Transactional
    public PipelineBuildDTO executePipeline(PipelineExecuteRequest pipelineRequest) {
        UUID pipelineId = pipelineRequest.getPipelineId();
        Optional<Pipeline> optionalPipeline = pipelineRepository.findById(pipelineId);

        if (optionalPipeline.isEmpty()) {
            throw new EntityNotFoundException("Pipeline with id " + pipelineId + " not found");
        }

        Pipeline pipeline = optionalPipeline.get();
        PipelineBuild pipelineBuild = createBuild(pipeline);

        buildRepository.save(pipelineBuild);

        pipelineExecutor.execute(pipelineBuild);

        return mapper.entityToDto(pipelineBuild);
    }

    //TODO: abstract this logic
    private PipelineBuild createBuild(final Pipeline pipeline) {
        List<StageBuild> stageBuilds = pipeline.getStages().stream().map(stage -> new StageBuild(stage)).toList();
        PipelineBuild pipelineBuild = new PipelineBuild(pipeline, stageBuilds);

        return pipelineBuild;
    }
}