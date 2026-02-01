package com.autobuild.pipeline.executor.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.dto.PipelineExecuteRequest;
import com.autobuild.pipeline.executor.service.PipelineExecutorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller for executing a pipeline.
 * 
 * @author Suvabrata Chowdhury
 */
@Tag(name = "Pipeline")
@RestController
@RequestMapping("/api/v1/execute")
public class PipelineExecutorController {

    @Autowired
    private PipelineExecutorService service;

    @Operation(summary = "Execute a pipeline")
    @PostMapping("/pipeline")
    public ResponseEntity<PipelineBuildDTO> executePipeline(@RequestBody @Valid PipelineExecuteRequest request)
            throws IOException {
        PipelineBuildDTO build = service.executePipeline(request);

        return ResponseEntity.accepted().body(build);
    }
}