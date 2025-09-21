package com.autobuild.pipeline.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.dto.Pipeline;
import com.autobuild.pipeline.service.PipelineService;

@RestController
@RequestMapping("/api/v1/pipeline")
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    @GetMapping("/{pipelineId}")
    public ResponseEntity<Pipeline> getPipeline(@PathVariable String pipelineId){
        // return "Requested id: " + pipelineId;
        // return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("To be implemented");

        return ResponseEntity.ok(pipelineService.getPipelineById(pipelineId));
    }

    @PostMapping
    public ResponseEntity<Pipeline> createPipeline(RequestEntity<Pipeline> pipelineRequest) {
        // return "Requested Creation: " + pipeline.getBody();
        // return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("To be implemented");

        Pipeline createdPipeline = pipelineService.createPipeline(pipelineRequest.getBody());
        return ResponseEntity.created(pipelineRequest.getUrl()).body(createdPipeline);
    }

    @PatchMapping
    public ResponseEntity<String> updatePipeline(RequestEntity<String> pipeline) {
        // return "Requested Update: " + pipeline.getBody();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("To be implemented");
    }

    @DeleteMapping
    public ResponseEntity<String> deletePipeline(RequestEntity<String> pipeline) {
        // return "Requested Delete: " + pipeline.getBody();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("To be implemented");
    }
}
