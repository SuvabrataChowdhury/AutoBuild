package com.autobuild.pipeline.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.autobuild.pipeline.configuration.AppConfig;
import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.testutility.DummyData;

// @ContextConfiguration(classes = {AppConfig.class})
@ExtendWith(SpringExtension.class)
public class PipelineMapperTest {
    // private ModelMapper mapper;

    private Pipeline pipeline = DummyData.pipeline;
    private PipelineDTO pipelineDTO = DummyData.pipelineDTO;

    @Autowired
    private PipelineMapper pipelineMapper;

    // @BeforeEach
    // public void setUp() {
    //     ReflectionTestUtils.setField(pipelineMapper, "mapper", mapper);
    // }

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
        assertEquals(null, pipeline.getId());
        assertEquals(pipelineDTO.getStages().size(), pipeline.getStages().size());
        assertEquals(pipelineDTO.getStages().get(0).getName(), pipeline.getStages().get(0).getName());
        assertEquals(null, pipeline.getStages().get(0).getId());
    }
}