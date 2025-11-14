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

import jakarta.persistence.EntityNotFoundException;

public class PipelineControllerTest {

    private PipelineDTO pipelineDTO = DummyData.getPipelineDTO();

    @Mock
    private PipelineService pipelineService;

    @InjectMocks
    private PipelineController controller ;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
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
}
