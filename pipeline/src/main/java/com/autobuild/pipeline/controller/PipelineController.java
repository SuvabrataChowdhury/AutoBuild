package com.autobuild.pipeline.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.dto.PipelineResponse;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.service.PipelineService;

/**
 * Controller for all CRUD operations on Pipeline.
 * @author Suvabrata Chowdhury
 */

//TODO: Need to refactor
//TODO: Define in terms of DTOs
@RestController
@RequestMapping("/api/v1/pipeline")
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineResponse> getPipelineById(@PathVariable String pipelineId){
        try {
            Pipeline pipeline = pipelineService.getPipelineById(pipelineId);

            if (null == pipeline) {
                PipelineResponse response = new PipelineResponse();
                response.setErrors(List.of("Pipeline with id " + pipelineId + " not found"));

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            PipelineResponse response = new PipelineResponse(pipeline);

            return ResponseEntity.ok().body(response);
        } catch (InvalidIdException e) {
            PipelineResponse response = new PipelineResponse();
            response.setErrors(List.of(e.getMessage()));

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<PipelineResponse> createPipeline(@RequestBody Pipeline pipelineRequest) {
        
        try {
            Pipeline createdPipeline = pipelineService.createPipeline(pipelineRequest);

            PipelineResponse response = new PipelineResponse(createdPipeline);

            URI location = URI.create("/pipeline/" + createdPipeline.getId());
            return ResponseEntity.created(location).body(response);
        } catch (DuplicateEntryException e) {
            PipelineResponse response = new PipelineResponse();
            response.setErrors(List.of(e.getMessage()));

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
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
