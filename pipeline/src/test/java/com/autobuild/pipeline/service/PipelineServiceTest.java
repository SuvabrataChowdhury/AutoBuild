package com.autobuild.pipeline.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.autobuild.pipeline.repository.PipelineRepository;

public class PipelineServiceTest {

    @Mock
    private PipelineRepository repository;

    @InjectMocks
    private PipelineService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPipelineByIdWithNullId() {
        // assertThrowsExactly(InvalidIdException.class, () -> service.getPipelineById(null));
    }

    // @Test
    // void testCreatePipeline() {

    // }
}
