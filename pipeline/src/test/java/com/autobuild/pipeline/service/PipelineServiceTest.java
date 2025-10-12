package com.autobuild.pipeline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.repository.PipelineRepository;

public class PipelineServiceTest {

    @Mock
    private PipelineRepository repository;

    @Mock
    private Pipeline pipeline;

    @InjectMocks
    private PipelineService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPipelineByIdWithEmptyId() {
        assertThrowsExactly(InvalidIdException.class, () -> service.getPipelineById(null));
        assertThrowsExactly(InvalidIdException.class, () -> service.getPipelineById(""));
        assertThrowsExactly(InvalidIdException.class, () -> service.getPipelineById(" "));
    }

    @Test
    public void testGetPipelineByIdWithInvalidId() {
        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString())).thenThrow(new IllegalArgumentException("Dummy msg: invalid id given"));
            
            assertThrowsExactly(InvalidIdException.class, () -> service.getPipelineById("1"));
        }
    }

    @Test
    public void testGetPipelineByIdWithValidId() throws InvalidIdException {
        UUID randomId = UUID.randomUUID();

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString())).thenReturn(randomId);
            doReturn(Optional.of(pipeline)).when(repository).findById(any(UUID.class));

            assertEquals(pipeline, service.getPipelineById("1"));
        }
    }

    @Test
    public void testGetPipelineByIdWithValidIdButNoPipeline() throws InvalidIdException {
        UUID randomId = UUID.randomUUID();

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString())).thenReturn(randomId);
            doReturn(Optional.ofNullable(null)).when(repository).findById(any(UUID.class));

            assertEquals(null, service.getPipelineById("1"));
        }
    }

    @Test
    public void testCreatePipelineWithNullPipeline() throws DuplicateEntryException {
        doThrow(IllegalArgumentException.class).when(repository).save(any(Pipeline.class));

        assertNull(service.createPipeline(null));
    }

    @Test
    public void testCreatePipelineWithValidPipeline() throws DuplicateEntryException {
        doReturn(pipeline).when(repository).save(any(Pipeline.class));

        assertEquals(pipeline, service.createPipeline(pipeline));
    }
}
