package com.autobuild.pipeline.executor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.execution.state.PipelineExecutionState;
import com.autobuild.pipeline.executor.service.PipelineBuildService;
import com.autobuild.pipeline.testutility.DummyData;

import jakarta.persistence.EntityNotFoundException;

public class PipelineBuildControllerTest {
    @Mock
    private PipelineBuildService service;

    @InjectMocks
    private PipelineBuildController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void getPipelineBuildTest() {
        UUID pipelineBuildId = UUID.randomUUID();

        PipelineBuildDTO dummyPipelineDTO = new PipelineBuildDTO(pipelineBuildId,UUID.randomUUID(),PipelineExecutionState.WAITING, List.of(DummyData.getStageBuildDTO()));

        doReturn(dummyPipelineDTO).when(service).getPipelineBuild(pipelineBuildId);

        ResponseEntity<PipelineBuildDTO> response = controller.getPipelineBuild(pipelineBuildId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(pipelineBuildId,response.getBody().getId());
    }

    @Test
    public void getPipelineBuildWithInvalidUUIDTest() {
        doThrow(new EntityNotFoundException("dummy exception")).when(service).getPipelineBuild(any(UUID.class));

        assertThrows(EntityNotFoundException.class, () -> controller.getPipelineBuild(UUID.randomUUID()));
    }

    @Test
    public void getLivePipelineBuildTest() {
        UUID pipelineBuildId = UUID.randomUUID();
        SseEmitter emitter = controller.getLivePipelineBuild(pipelineBuildId);

        ArgumentCaptor<SseEmitter> emitterCaptor = ArgumentCaptor.forClass(SseEmitter.class);
        ArgumentCaptor<UUID> pipelineIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(service, times(1)).addSubscriber(emitterCaptor.capture(), pipelineIdCaptor.capture());

        assertEquals(pipelineBuildId, pipelineIdCaptor.getValue());
        assertEquals(emitter, emitterCaptor.getValue());
    }

    @Test
    public void getLivePipelineBuildErrorTest() {
        doThrow(new UnsupportedOperationException("Dummy exception")).when(service).addSubscriber(any(SseEmitter.class), any(UUID.class));

        assertThrows(UnsupportedOperationException.class, () -> controller.getLivePipelineBuild(UUID.randomUUID()));
    }

    @Test
    public void deletePipelineBuild() throws IOException {
        UUID pipelineBuildId = UUID.randomUUID();

        controller.deletePipelineBuild(pipelineBuildId);
        verify(service, times(1)).deletePipelineBuild(pipelineBuildId);
    }
}
