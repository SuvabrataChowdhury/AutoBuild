package com.autobuild.pipeline.definiton.controller;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobuild.pipeline.definiton.dto.PipelineDTO;
import com.autobuild.pipeline.definiton.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.definiton.exceptions.InvalidIdException;
import com.autobuild.pipeline.definiton.service.PipelineService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import com.autobuild.pipeline.definiton.dto.StageDTO;

/**
 * Controller for all CRUD operations on Pipeline.
 * 
 * @author Suvabrata Chowdhury
 */

@RestController
@RequestMapping("/api/v1/pipeline")
// @Validated
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/{pipelineId}")
    public ResponseEntity<PipelineDTO> getPipelineById(@PathVariable String pipelineId)
            throws InvalidIdException, IOException {

        PipelineDTO pipeline = pipelineService.getPipelineById(pipelineId);

        return ResponseEntity.ok().body(pipeline);
    }

    @PostMapping
    public ResponseEntity<PipelineDTO> createPipeline(@RequestBody @Valid PipelineDTO pipelineRequest)
            throws DuplicateEntryException, IOException {

        PipelineDTO createdPipeline = pipelineService.createPipeline(pipelineRequest);
        URI location = URI.create("/api/v1/pipeline/" + createdPipeline.getId());

        return ResponseEntity.created(location).body(createdPipeline);
    }

    @PatchMapping("/{pipelineId}")
    public ResponseEntity<PipelineDTO> modifyPipeline(
            @PathVariable String pipelineId,
            @RequestBody Map<String, Object> body)
            throws InvalidIdException, IOException, DuplicateEntryException {

        // Check if the request contains a pipeline name
        if (body.containsKey("name") && body.get("name") instanceof String) {
            String newName = (String) body.get("name");
            PipelineDTO updatedPipeline = pipelineService.updatePipelineName(pipelineId, newName);
            return ResponseEntity.ok(updatedPipeline);
        }

        // Check if the request contains stages
        if (body.containsKey("stages") && body.get("stages") instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> stagesRaw = (List<Object>) body.get("stages");
            
            if (stagesRaw == null || stagesRaw.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Convert List<LinkedHashMap> to List<StageDTO>
            List<StageDTO> stages = stagesRaw.stream()
                    .map(obj -> objectMapper.convertValue(obj, StageDTO.class))
                    .toList();

            PipelineDTO updatedPipeline = pipelineService.modifyPipelineStages(pipelineId, stages);
            return ResponseEntity.ok(updatedPipeline);
        }

        // If neither "name" nor "stages" is provided, return a bad request
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{pipelineId}")
    public ResponseEntity<String> deletePipeline(@PathVariable String pipelineId)
            throws IOException, InvalidIdException {
        pipelineService.deletePipelineById(pipelineId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
