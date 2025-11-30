package com.autobuild.pipeline.executor.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.testutility.DummyData;

public class PipelineBuildMapperTest {
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private PipelineBuildMapper pipelineBuildMapper;


    private ArgumentCaptor<PipelineBuild> pipelineBuildCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        pipelineBuildCaptor = ArgumentCaptor.forClass(PipelineBuild.class);
    }

    @Test
    public void entityToDtoTest() {
        PipelineBuild dummyBuild = DummyData.getPipelineBuild();
        PipelineBuildDTO dummyBuildDTO = DummyData.getPipelineBuildDTO();

        doReturn(dummyBuildDTO).when(mapper).map(any(PipelineBuild.class), eq(PipelineBuildDTO.class));
        
        PipelineBuildDTO mappedDto = pipelineBuildMapper.entityToDto(dummyBuild);

        assertEquals(dummyBuildDTO, mappedDto);

        verify(mapper, times(1)).map(pipelineBuildCaptor.capture(), eq(PipelineBuildDTO.class));

        assertNotNull(pipelineBuildCaptor.getValue());
        assertEquals(dummyBuild, pipelineBuildCaptor.getValue());
    }

    @Test
    public void entityToDtoErrorTest() {
        assertThrows(IllegalArgumentException.class, () -> pipelineBuildMapper.entityToDto(null));
    }
}
