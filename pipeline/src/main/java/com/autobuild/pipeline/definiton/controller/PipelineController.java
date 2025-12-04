package com.autobuild.pipeline.definiton.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.definiton.dto.PipelineDTO;
import com.autobuild.pipeline.definiton.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.definiton.exceptions.InvalidIdException;
import com.autobuild.pipeline.definiton.service.PipelineService;

import jakarta.validation.Valid;

/**
 * Controller for all CRUD operations on Pipeline.
 * 
 * @author Suvabrata Chowdhury & Baibhab Dey
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/pipeline")
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineDTO> getPipelineById(@PathVariable String pipelineId)
            throws InvalidIdException, IOException {
        return ResponseEntity.ok(pipelineService.getPipelineById(pipelineId));
    }

    @GetMapping
    public ResponseEntity<List<PipelineDTO>> getAllPipelines() {
        return ResponseEntity.ok(pipelineService.getAllPipelines());
    }

    @PostMapping
    public ResponseEntity<PipelineDTO> createPipeline(@RequestBody @Valid PipelineDTO pipelineRequest)
            throws DuplicateEntryException, IOException, InvalidIdException { // added InvalidIdException
        PipelineDTO createdPipeline = pipelineService.createPipeline(pipelineRequest);
        URI location = URI.create("/api/v1/pipeline/" + createdPipeline.getId());
        return ResponseEntity.created(location).body(createdPipeline);
    }

    @PatchMapping("/{pipelineId}")
    public ResponseEntity<PipelineDTO> modifyPipeline(
            @PathVariable String pipelineId,
            @RequestBody PipelineDTO patchRequest)
            throws InvalidIdException, IOException, DuplicateEntryException {
        return ResponseEntity.ok(pipelineService.modifyPipeline(pipelineId, patchRequest));
    }

    @DeleteMapping("/{pipelineId}")
    public ResponseEntity<Void> deletePipeline(@PathVariable String pipelineId)
            throws IOException, InvalidIdException {
        pipelineService.deletePipelineById(pipelineId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{pipelineId}")
    public ResponseEntity<PipelineDTO> updatePipeline(
            @PathVariable String pipelineId,
            @RequestBody @Valid PipelineDTO putRequest)
            throws InvalidIdException, IOException, DuplicateEntryException {
        return ResponseEntity.ok(pipelineService.replacePipeline(pipelineId, putRequest));
    }
}