package com.autobuild.pipeline.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Stage;
import com.autobuild.pipeline.testutility.DummyData;

public class StageMapperTest {
    private ModelMapper mapper = new ModelMapper();
    
    private Stage stage = DummyData.stage;
    private StageDTO dto = DummyData.stageDto;

    // @InjectMocks
    private StageMapper stageMapper = new StageMapper();

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(stageMapper, "mapper", mapper);
    }

    @Test
    public void givenStageEntityGetStageDTO() {

        StageDTO convertedStageDTO = stageMapper.entityToDto(stage);
        
        assertEquals(stage.getId(), convertedStageDTO.getId());
        assertEquals(stage.getName(), convertedStageDTO.getName());
        assertEquals(stage.getCommand(), convertedStageDTO.getCommand());
        assertEquals(stage.getScriptType(), convertedStageDTO.getScriptType());
    }

    @Test
    public void givenNullEntityGetStageDTO() {
        assertThrows(IllegalArgumentException.class, () -> stageMapper.entityToDto(null));
    }

    @Test
    public void givenStageDTOGetStageEntity() {
        Stage convertedStage = stageMapper.dtoToEntity(dto);

        assertEquals(null, convertedStage.getId());
        assertEquals(dto.getName(), convertedStage.getName());
        assertEquals(dto.getCommand(), convertedStage.getCommand());
        assertEquals(dto.getScriptType(), convertedStage.getScriptType());
        assertNull(convertedStage.getPath());
    } 
    
    @Test
    public void givenNullDTOGetStageEntity() {
        assertThrows(IllegalArgumentException.class, () -> stageMapper.dtoToEntity(null));
    }
}
