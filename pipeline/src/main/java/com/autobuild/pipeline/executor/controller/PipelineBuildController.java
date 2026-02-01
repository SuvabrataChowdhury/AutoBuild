package com.autobuild.pipeline.executor.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.service.PipelineBuildService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for Pipeline Build.
 * 
 * @author Suvabrata Chowdhury
 */

@Slf4j
@Tag(name = "Pipeline Build", description = "Operations related to builds of a pipeline")
@RestController
@RequestMapping("/api/v1/pipeline/build")
public class PipelineBuildController {

    @Autowired
    private PipelineBuildService service;

    @Operation(summary = "Get a pipeline build")
    @GetMapping("/{pipelineBuildId}")
    public ResponseEntity<PipelineBuildDTO> getPipelineBuild(@PathVariable UUID pipelineBuildId) {
        return ResponseEntity.ok(service.getPipelineBuild(pipelineBuildId));
    }

    @Operation(summary = "Get all pipeline builds")
    @GetMapping
    public ResponseEntity<List<PipelineBuildDTO>> getAllBuilds() {
        return ResponseEntity.ok(service.getAllBuilds());
    }

    @Operation(summary = "Delete a pipeline build")
    @DeleteMapping("/{pipelineBuildId}")
    public ResponseEntity<Void> deletePipelineBuild(@PathVariable UUID pipelineBuildId) throws IOException {
        service.deletePipelineBuild(pipelineBuildId);
        return ResponseEntity.noContent().build();
    }
}
