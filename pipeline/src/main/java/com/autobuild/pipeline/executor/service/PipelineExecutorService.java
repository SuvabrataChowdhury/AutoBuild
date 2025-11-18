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

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.definiton.repository.PipelineRepository;
import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.dto.PipelineExecuteRequest;
import com.autobuild.pipeline.executor.dto.mapper.PipelineBuildMapper;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.entity.StageBuild;
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
    private PipelineBuildMapper mapper;

    public PipelineBuildDTO executePipeline(PipelineExecuteRequest pipelineRequest) {
        UUID pipelineId = pipelineRequest.getPipelineId();
        Optional<Pipeline> optionalPipeline = pipelineRepository.findById(pipelineId);

        if (optionalPipeline.isEmpty()) {
            throw new EntityNotFoundException("Pipeline with id " + pipelineId + " not found");
        }

        Pipeline pipeline = optionalPipeline.get();
        PipelineBuild pipelineBuild = createBuild(pipeline);

        buildRepository.save(pipelineBuild);

        //TODO: Abstract this logic
        new Thread(() -> {

            try {
                Thread.sleep(2000); //Dummy wait time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.startBuild(pipelineBuild);
        }).start(); //async start job //TODO: use queue and thread limit

        return mapper.entityToDto(pipelineBuild);
    }

    //TODO: abstract this logic
    private PipelineBuild createBuild(final Pipeline pipeline) {
        List<StageBuild> stageBuilds = pipeline.getStages().stream().map(stage -> new StageBuild(stage)).toList();
        PipelineBuild pipelineBuild = new PipelineBuild(pipeline, stageBuilds);

        return pipelineBuild;
    }

    //TODO: abstract this logic
    private void startBuild(PipelineBuild pipelineBuild) {
        Pipeline pipeline = pipelineBuild.getPipeline();

        try {
            pipeline.getStages().forEach(stage -> {
                ProcessBuilder stageProcessBuilder = new ProcessBuilder(stage.getPath());
                try {
                    Process stageProcess = stageProcessBuilder.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stageProcess.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            log.info("OP: " + line);
                        }                        
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }catch (UncheckedIOException exception) {
            exception.getCause().printStackTrace();
        }
        
        log.info("Build started for: " + pipeline.getId());
    }
}