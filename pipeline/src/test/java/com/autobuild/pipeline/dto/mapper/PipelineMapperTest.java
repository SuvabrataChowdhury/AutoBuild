package com.autobuild.pipeline.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.testutility.DummyData;

public class PipelineMapperTest {
    private ModelMapper mapper = new ModelMapper();

    private Pipeline pipeline = DummyData.pipeline;
    private PipelineDTO pipelineDTO = DummyData.pipelineDTO;

    @InjectMocks
    PipelineMapper pipelineMapper = new PipelineMapper();

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(pipelineMapper, "mapper", mapper);
    }

    @Test
    public void whenGivenPipelineEntityGetPipelineDTO() {

        PipelineDTO convertedDto = pipelineMapper.entityToDto(pipeline);

        assertEquals(pipeline.getId(), convertedDto.getId());
        assertEquals(pipeline.getName(), convertedDto.getName());
        assertEquals(pipeline.getStages().size(),convertedDto.getStages().size());
        assertEquals(pipeline.getStages().get(0).getName(), convertedDto.getStages().get(0).getName());
        assertEquals(pipeline.getStages().get(0).getId(), convertedDto.getStages().get(0).getId());
    }

    @Test
    public void whenGivenPipelineDTOGetPipelineEntity() {
        Pipeline pipeline = pipelineMapper.dtoToEntity(pipelineDTO);

        assertEquals(pipelineDTO.getName(), pipeline.getName());
        assertEquals(null, pipeline.getId());
        assertEquals(pipelineDTO.getStages().size(), pipeline.getStages().size());
        assertEquals(pipelineDTO.getStages().get(0).getName(), pipeline.getStages().get(0).getName());
        assertEquals(null, pipeline.getStages().get(0).getId());
    }

    @Test
    public void whenGivenNullPipelineGetPipelineDTO() {
        assertThrows(IllegalArgumentException.class, () -> pipelineMapper.entityToDto(null));
    }

    @Test
    public void whenGivenNullDTPGetPipeline() {
        assertThrows(IllegalArgumentException.class, () -> pipelineMapper.dtoToEntity(null));
    }
}