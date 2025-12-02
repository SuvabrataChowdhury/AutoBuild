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

import com.autobuild.pipeline.executor.dto.StageBuildDTO;
import com.autobuild.pipeline.executor.entity.StageBuild;
import com.autobuild.pipeline.testutility.DummyData;

public class StageBuildMapperTest {
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private StageBuildMapper StageBuildMapper;


    private ArgumentCaptor<StageBuild> StageBuildCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        StageBuildCaptor = ArgumentCaptor.forClass(StageBuild.class);
    }

    @Test
    public void entityToDtoTest() {
        StageBuild dummyBuild = DummyData.getStageBuild();
        StageBuildDTO dummyBuildDTO = DummyData.getStageBuildDTO();

        doReturn(dummyBuildDTO).when(mapper).map(any(StageBuild.class), eq(StageBuildDTO.class));
        
        StageBuildDTO mappedDto = StageBuildMapper.entityToDto(dummyBuild);

        assertEquals(dummyBuildDTO, mappedDto);

        verify(mapper, times(1)).map(StageBuildCaptor.capture(), eq(StageBuildDTO.class));

        assertNotNull(StageBuildCaptor.getValue());
        assertEquals(dummyBuild, StageBuildCaptor.getValue());
    }

    @Test
    public void entityToDtoErrorTest() {
        assertThrows(IllegalArgumentException.class, () -> StageBuildMapper.entityToDto(null));
    }
}