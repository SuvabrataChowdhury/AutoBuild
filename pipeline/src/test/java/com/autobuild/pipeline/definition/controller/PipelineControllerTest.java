package com.autobuild.pipeline.definition.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.autobuild.pipeline.definiton.controller.PipelineController;
import com.autobuild.pipeline.definiton.dto.PipelineDTO;
import com.autobuild.pipeline.definiton.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.definiton.exceptions.InvalidIdException;
import com.autobuild.pipeline.definiton.service.PipelineService;
import com.autobuild.pipeline.testutility.DummyData;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Spy;

import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.autobuild.pipeline.definiton.dto.StageDTO;

public class PipelineControllerTest {

    private PipelineDTO pipelineDTO = DummyData.getPipelineDTO();

    @Mock
    private PipelineService pipelineService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper(); 

    @InjectMocks
    private PipelineController controller ;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        assert objectMapper != null; // sanity check to prevent NPE
    }

    @Test
    public void testGetPipelineWithNoPipeline() throws InvalidIdException, IOException {
        doThrow(new EntityNotFoundException("Dummy Exception")).when(pipelineService).getPipelineById(anyString());

        assertThrows(EntityNotFoundException.class, () -> controller.getPipelineById("1"));
    }

    @Test
    public void testGetPipelineWithPipeline() throws InvalidIdException, IOException {
        doReturn(pipelineDTO).when(pipelineService).getPipelineById(anyString());

        ResponseEntity<PipelineDTO> getPipelineResponse = (ResponseEntity<PipelineDTO>) controller.getPipelineById("1");

        assertEquals(HttpStatus.OK, getPipelineResponse.getStatusCode());
        assertEquals(pipelineDTO, getPipelineResponse.getBody());
    }

    @Test
    public void testGetPipelineWithInvalidId() throws InvalidIdException, IOException {
        doThrow(
            new InvalidIdException("Dummy Exception")
        ).when(pipelineService).getPipelineById(anyString());

        assertThrows(InvalidIdException.class, () -> controller.getPipelineById("1"));
    }

    @Test
    public void testCreatePipelineWithPipeline() throws DuplicateEntryException, IOException {
        doReturn(pipelineDTO).when(pipelineService).createPipeline(any(PipelineDTO.class));

        ResponseEntity<PipelineDTO> createPipelineResponse = controller.createPipeline(pipelineDTO);

        assertEquals(HttpStatus.CREATED, createPipelineResponse.getStatusCode());
        assertEquals(pipelineDTO, createPipelineResponse.getBody());
        assertEquals("/api/v1/pipeline/" + pipelineDTO.getId(), createPipelineResponse.getHeaders().get("location").get(0));
    }

    @Test
    public void testCreatePipelineWithDuplicateStages() throws DuplicateEntryException, IOException {
        doThrow(new DuplicateEntryException("Dummy exception")).when(pipelineService).createPipeline(any(PipelineDTO.class));

        assertThrows(DuplicateEntryException.class, () -> controller.createPipeline(pipelineDTO));
    }

    @Test
    public void testDeletePipeline() throws IOException, InvalidIdException {
        doNothing().when(pipelineService).deletePipelineById(anyString());

        ResponseEntity<String> response = controller.deletePipeline("abc");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testModifyPipelineStagesCreate() throws Exception {
        StageDTO newStage = new StageDTO(null, "deploy", "bash", "#!/bin/bash\necho DEPLOY");
        PipelineDTO modified = DummyData.getPipelineDTO();
        modified.setStages(new ArrayList<>(modified.getStages()));
        StageDTO createdStage = new StageDTO(UUID.randomUUID(), newStage.getName(), 
                newStage.getScriptType(), newStage.getCommand());
        modified.getStages().add(createdStage);

        doReturn(modified).when(pipelineService).modifyPipelineStages(anyString(), any(List.class));

        Map<String, Object> body = new HashMap<>();
        body.put("stages", List.of(newStage));

        ResponseEntity<PipelineDTO> response = controller.modifyPipeline("pid", body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(modified.getStages().size(), response.getBody().getStages().size());
    }

    @Test
    public void testModifyPipelineStagesUpdate() throws Exception {
        PipelineDTO base = DummyData.getPipelineDTO();
        base.setStages(new ArrayList<>(base.getStages()));
        StageDTO existing = base.getStages().get(0);
        StageDTO updateReq = new StageDTO(existing.getId(), "renamed", 
                existing.getScriptType(), "#!/bin/bash\necho UPDATED");

        existing.setName("renamed");
        existing.setCommand(updateReq.getCommand());
        doReturn(base).when(pipelineService).modifyPipelineStages(anyString(), any(List.class));

        Map<String, Object> body = new HashMap<>();
        body.put("stages", List.of(updateReq));

        ResponseEntity<PipelineDTO> response = controller.modifyPipeline("pid", body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("renamed", response.getBody().getStages().get(0).getName());
    }

    @Test
    public void testModifyPipelineStagesDelete() throws Exception {
        PipelineDTO base = DummyData.getPipelineDTO();
        base.setStages(new ArrayList<>(base.getStages()));
        StageDTO toDelete = base.getStages().get(0);
        base.getStages().remove(0);

        doReturn(base).when(pipelineService).modifyPipelineStages(anyString(), any(List.class));

        StageDTO deleteReq = new StageDTO(toDelete.getId(), null, null, null);
        Map<String, Object> body = new HashMap<>();
        body.put("stages", List.of(deleteReq));

        ResponseEntity<PipelineDTO> response = controller.modifyPipeline("pid", body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        boolean present = response.getBody().getStages().stream()
                .anyMatch(s -> s.getId().equals(toDelete.getId()));
        assertEquals(false, present);
    }

    @Test
    public void testModifyPipelineStagesBadRequestEmpty() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("stages", List.of());
        ResponseEntity<PipelineDTO> response = controller.modifyPipeline("pid", body);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testModifyPipelineStagesDuplicateEntry() throws Exception {
        StageDTO newStage = new StageDTO(null, "duplicate", "bash", "#!/bin/bash\necho X");
        Map<String, Object> body = new HashMap<>();
        body.put("stages", List.of(newStage));

        doThrow(new DuplicateEntryException("duplicate")).when(pipelineService)
                .modifyPipelineStages(anyString(), any(List.class));

        assertThrows(DuplicateEntryException.class, () -> controller.modifyPipeline("pid", body));
    }

    @Test
    public void testModifyPipelineStagesInvalidId() throws Exception {
        StageDTO newStage = new StageDTO(null, "stage", "bash", "#!/bin/bash\necho X");
        Map<String, Object> body = new HashMap<>();
        body.put("stages", List.of(newStage));

        doThrow(new InvalidIdException("invalid")).when(pipelineService)
                .modifyPipelineStages(anyString(), any(List.class));

        assertThrows(InvalidIdException.class, () -> controller.modifyPipeline("pid", body));
    }

    @Test
    public void testUpdatePipelineName() throws Exception {
        PipelineDTO updated = DummyData.getPipelineDTO();
        updated.setName("New Pipeline Name");

        doReturn(updated).when(pipelineService).updatePipelineName(anyString(), anyString());

        Map<String, Object> body = new HashMap<>();
        body.put("name", "New Pipeline Name");

        ResponseEntity<PipelineDTO> response = controller.modifyPipeline("pid", body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Pipeline Name", response.getBody().getName());
    }

    @Test
    public void testModifyPipelineBadRequestNoNameOrStages() throws Exception {
        Map<String, Object> body = new HashMap<>();
        ResponseEntity<PipelineDTO> response = controller.modifyPipeline("pid", body);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
