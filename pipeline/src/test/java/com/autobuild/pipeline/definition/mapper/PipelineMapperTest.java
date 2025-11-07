package com.autobuild.pipeline.definition.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.autobuild.pipeline.configuration.MapperConfig;
import com.autobuild.pipeline.definiton.dto.PipelineDTO;
import com.autobuild.pipeline.definiton.dto.mapper.PipelineMapper;
import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.testutility.DummyData;

@SpringJUnitConfig
@ContextConfiguration(classes = {MapperConfig.class,PipelineMapper.class})
public class PipelineMapperTest {

    private Pipeline pipeline = DummyData.getPipeline();
    private PipelineDTO pipelineDTO = DummyData.getPipelineDTO();

    @Autowired
    private PipelineMapper pipelineMapper;

    @Test
    public void whenGivenPipelineEntityGetPipelineDTO() {
        PipelineDTO convertedDto = pipelineMapper.entityToDto(pipeline);

        assertDto(convertedDto);
    }

    @Test
    public void whenGivenPipelineDTOGetPipelineEntity() {
        Pipeline pipeline = pipelineMapper.dtoToEntity(pipelineDTO);

        assertEntity(pipeline);
    }

    @Test
    public void whenGivenNullPipelineGetPipelineDTO() {
        assertThrows(IllegalArgumentException.class, () -> pipelineMapper.entityToDto(null));
    }

    @Test
    public void whenGivenNullDTPGetPipeline() {
        assertThrows(IllegalArgumentException.class, () -> pipelineMapper.dtoToEntity(null));
    }

    @Test
    public void multipleMapInvocationTest() {
        Pipeline p1 = pipelineMapper.dtoToEntity(pipelineDTO);
        assertEntity(p1);

        pipelineDTO.setId(UUID.randomUUID());

        Pipeline p2 = pipelineMapper.dtoToEntity(pipelineDTO);
        assertEntity(p2);
    }

    private void assertDto(PipelineDTO convertedDto) {
        assertEquals(pipeline.getId(), convertedDto.getId());
        assertEquals(pipeline.getName(), convertedDto.getName());
        assertEquals(pipeline.getStages().size(),convertedDto.getStages().size());
        assertEquals(pipeline.getStages().get(0).getName(), convertedDto.getStages().get(0).getName());
        assertEquals(pipeline.getStages().get(0).getId(), convertedDto.getStages().get(0).getId());
    }

    private void assertEntity(Pipeline pipeline) {
        assertEquals(pipelineDTO.getName(), pipeline.getName());
        assertEquals(pipelineDTO.getId(), pipeline.getId());
        assertEquals(pipelineDTO.getStages().size(), pipeline.getStages().size());
        assertEquals(pipelineDTO.getStages().get(0).getName(), pipeline.getStages().get(0).getName());
        assertEquals(pipelineDTO.getStages().get(0).getId(), pipeline.getStages().get(0).getId());
    }
}