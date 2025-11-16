package com.autobuild.pipeline.executor.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.definiton.repository.PipelineRepository;
import com.autobuild.pipeline.executor.dto.PipelineExecuteRequest;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.entity.StageBuild;
import com.autobuild.pipeline.executor.repository.PipelineBuildRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PipelineExecutorService {
    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private PipelineBuildRepository buildRepository;

    public PipelineBuild executePipeline(PipelineExecuteRequest pipelineRequest) {
        UUID pipelineId = pipelineRequest.getPipelineId();
        Optional<Pipeline> optionalPipeline = pipelineRepository.findById(pipelineId);

        if(optionalPipeline.isEmpty()) {
            throw new EntityNotFoundException("Pipeline with id " + pipelineId + " not found");
        }

        Pipeline pipeline = optionalPipeline.get();
        PipelineBuild pipelineBuild = createBuild(pipeline);

        buildRepository.save(pipelineBuild);

        //TODO: Abstract this logic
        new Thread(() -> {

            try {
                Thread.sleep(3000); //Dummy wait time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.startBuild(pipeline);
        }).start(); //async start job //TODO: use queue and thread limit

        return pipelineBuild;
    }

    //TODO: abstract this logic
    private PipelineBuild createBuild(final Pipeline pipeline) {
        List<StageBuild> stageBuilds = pipeline.getStages().stream().map(stage -> new StageBuild(stage)).toList();
        PipelineBuild pipelineBuild = new PipelineBuild(pipeline, stageBuilds);

        return pipelineBuild;
    }

    //TODO: abstract this logic
    private void startBuild(Pipeline pipeline) {
        log.info("Build started for: " + pipeline.getId());
    }
}