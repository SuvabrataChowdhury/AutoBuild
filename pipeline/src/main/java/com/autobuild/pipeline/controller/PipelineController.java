package com.autobuild.pipeline.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.service.PipelineService;

/**
 * Controller for all CRUD operations on Pipeline.
 * @author Suvabrata Chowdhury
 */

//TODO: Need to refactor
@RestController
@RequestMapping("/api/v1/pipeline")
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    //TODO: send errors through global exception handler
    @GetMapping("/{pipelineId}")
    public ResponseEntity<?> getPipelineById(@PathVariable String pipelineId){
        try {
            PipelineDTO pipeline = pipelineService.getPipelineById(pipelineId);

            if (null == pipeline) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Pipeline with id " + pipelineId + " not found");
            }

            return ResponseEntity.ok().body(pipeline);
        } catch (InvalidIdException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //TODO: send errors through global exception handler
    @PostMapping
    public ResponseEntity<?> createPipeline(@RequestBody PipelineDTO pipelineRequest) {
        try {
            PipelineDTO createdPipeline = pipelineService.createPipeline(pipelineRequest);
            URI location = URI.create("/api/v1/pipeline/" + createdPipeline.getId());

            return ResponseEntity.created(location).body(createdPipeline);
        } catch (DuplicateEntryException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // @PatchMapping
    // public ResponseEntity<String> updatePipeline(RequestEntity<String> pipeline) {
    //     // return "Requested Update: " + pipeline.getBody();
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("To be implemented");

    //     // pipelineService.deletePipeline()
    // }

    // @DeleteMapping
    // public ResponseEntity<String> deletePipeline(RequestEntity<String> pipeline) {
    //     // return "Requested Delete: " + pipeline.getBody();
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("To be implemented");
    // }
}
