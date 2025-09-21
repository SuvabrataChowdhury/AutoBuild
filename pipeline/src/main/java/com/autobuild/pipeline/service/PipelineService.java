package com.autobuild.pipeline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobuild.pipeline.dto.Pipeline;
import com.autobuild.pipeline.repository.PipelineRepository;

@Service
public class PipelineService {
    @Autowired
    private PipelineRepository repository;

    public Pipeline createPipeline(final Pipeline pipeline) {
        return repository.save(pipeline);
    }

    public Pipeline getPipelineById(final String pipelineId) {
        return repository.getReferenceById(pipelineId);
    }
}