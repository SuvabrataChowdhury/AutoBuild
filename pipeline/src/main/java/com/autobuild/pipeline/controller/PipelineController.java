package com.autobuild.pipeline.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.service.PipelineService;

@RestController
@RequestMapping("/api/v1/pipeline")
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    @GetMapping("/{pipelineId}")
    public ResponseEntity<Pipeline> getPipelineById(@PathVariable String pipelineId){
        try {
            Pipeline pipeline = pipelineService.getPipelineById(pipelineId);

            if(null == pipeline) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().body(pipeline);
        } catch (InvalidIdException e) {

            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Pipeline> createPipeline(@RequestBody Pipeline pipelineRequest) {
        Pipeline createdPipeline = pipelineService.createPipeline(pipelineRequest);

        // URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pipelineid}").buildAndExpand(createdPipeline.getId()).toUri();
        URI location = URI.create("/pipeline/" + createdPipeline.getId());

        return ResponseEntity.created(location).body(createdPipeline);
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
