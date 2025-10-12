package com.autobuild.pipeline.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.service.PipelineService;

public class PipelineControllerTest {

    @Mock
    private PipelineService pipelineService;

    @Mock
    private Pipeline pipeline;

    @InjectMocks
    private PipelineController controller ;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPipelineWithNoPipeline() throws InvalidIdException {
        doReturn(null).when(pipelineService).getPipelineById(anyString());

        ResponseEntity<Pipeline> getPipelineResponse = controller.getPipelineById("1");

        assertEquals(HttpStatus.NOT_FOUND, getPipelineResponse.getStatusCode());
    }

    @Test
    void testGetPipelineWithPipeline() throws InvalidIdException {
        doReturn(pipeline).when(pipelineService).getPipelineById(anyString());

        ResponseEntity<Pipeline> getPipelineResponse = controller.getPipelineById("1");

        assertEquals(HttpStatus.OK, getPipelineResponse.getStatusCode());
        assertEquals(pipeline, getPipelineResponse.getBody());
        // assertEquals(, getPipelineResponse);
    }

    //TODO: Test in integration test
    //Need MockMVC for it
    // @Test
    // void testGetPipelineWithException() {
    //     doThrow(new RuntimeException("Dummy Exception")).when(pipelineService).getPipelineById(anyString());

    //     ResponseEntity<Pipeline> getPipelineResponse = controller.getPipelineById("1");

    //     assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getPipelineResponse.getStatusCode());
    // }

    @Test
    void testCreatePipelineWithPipeline() {
        doReturn(pipeline).when(pipelineService).createPipeline(any(Pipeline.class));

        UUID randomPipelineID = UUID.randomUUID();
        doReturn(randomPipelineID).when(pipeline).getId();

        ResponseEntity<Pipeline> createPipelineResponse = controller.createPipeline(mock(Pipeline.class));

        assertEquals(HttpStatus.CREATED, createPipelineResponse.getStatusCode());
        assertEquals(pipeline, createPipelineResponse.getBody());
        assertEquals("/pipeline/" + randomPipelineID, createPipelineResponse.getHeaders().get("location").get(0));
    }
}
