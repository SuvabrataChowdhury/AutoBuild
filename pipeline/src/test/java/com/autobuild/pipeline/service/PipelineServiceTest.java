package com.autobuild.pipeline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.mapper.PipelineMapper;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.exceptions.InvalidIdException;
import com.autobuild.pipeline.repository.PipelineRepository;
import com.autobuild.pipeline.testutility.DummyData;
import com.autobuild.pipeline.validator.PipelineValidator;

import jakarta.persistence.EntityNotFoundException;

public class PipelineServiceTest {

    private PipelineDTO pipelineDTO = DummyData.pipelineDTO;
    private Pipeline pipeline = DummyData.pipeline;

    @Mock
    private PipelineRepository repository;

    @Mock
    private PipelineValidator validator;

    @Mock
    private PipelineMapper mapper;

    @InjectMocks
    private PipelineService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        doReturn(pipelineDTO).when(mapper).entityToDto(any(Pipeline.class));
        doReturn(pipeline).when(mapper).dtoToEntity(any(PipelineDTO.class));
    }

    @Test
    public void testGetPipelineByIdWithEmptyId() {
        assertThrows(InvalidIdException.class, () -> service.getPipelineById(null));
        assertThrows(InvalidIdException.class, () -> service.getPipelineById(""));
        assertThrows(InvalidIdException.class, () -> service.getPipelineById(" "));
    }

    @Test
    public void testGetPipelineByIdWithInvalidId() {
        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString())).thenThrow(new IllegalArgumentException("Dummy msg: invalid id given"));
            
            assertThrows(InvalidIdException.class, () -> service.getPipelineById("1"));
        }
    }

    @Test
    public void testGetPipelineByIdWithValidId() throws InvalidIdException {
        UUID randomId = UUID.randomUUID();

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString())).thenReturn(randomId);
            doReturn(Optional.of(pipeline)).when(repository).findById(any(UUID.class));
            doReturn(pipelineDTO).when(mapper).entityToDto(pipeline);

            assertEquals(pipelineDTO, service.getPipelineById("1"));
        }
    }

    @Test
    public void testGetPipelineByIdWithValidIdButNoPipeline() throws InvalidIdException {
        UUID randomId = UUID.randomUUID();

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString())).thenReturn(randomId);
            doReturn(Optional.ofNullable(null)).when(repository).findById(any(UUID.class));

            assertThrows(EntityNotFoundException.class, () -> service.getPipelineById("1"));
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
        doReturn(pipelineDTO).when(mapper).entityToDto(pipeline);

        assertEquals(pipelineDTO, service.createPipeline(pipelineDTO));
    }
    
    @Test
    public void testCreatePipelineWithDuplicateStageName() {
        Errors mockValidationErrors = mock(Errors.class);

        doReturn(mockValidationErrors).when(validator).validatePipeline(pipelineDTO);
        doReturn(List.of(mock(ObjectError.class))).when(mockValidationErrors).getAllErrors();
        doReturn(true).when(mockValidationErrors).hasErrors();

        assertThrows(DuplicateEntryException.class, () -> service.createPipeline(pipelineDTO));
    }

    @Test
    public void testCreatePipelineWithDuplicatePipelineName() {
        doThrow(new DataIntegrityViolationException("Dummy Exception")).when(repository).save(pipeline);

        assertThrows(DuplicateEntryException.class, () -> service.createPipeline(pipelineDTO));
    }
}
