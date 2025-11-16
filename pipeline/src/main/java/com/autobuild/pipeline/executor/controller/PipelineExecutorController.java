package com.autobuild.pipeline.executor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.dto.PipelineExecuteRequest;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.service.PipelineExecutorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/execute")
public class PipelineExecutorController {

    @Autowired
    private PipelineExecutorService service;

    @PostMapping("/pipeline")
    public ResponseEntity<PipelineBuildDTO> executePipeline(@RequestBody @Valid PipelineExecuteRequest request) {
        PipelineBuildDTO build = service.executePipeline(request);

        return ResponseEntity.accepted().body(build);
    }
}