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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.autobuild.pipeline.executor.service.PipelineBuildLiveService;

public class PipelineBuildLiveControllerTest {
    @Mock
    private PipelineBuildLiveService service;

    @InjectMocks
    private PipelineBuildLiveController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
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
}
