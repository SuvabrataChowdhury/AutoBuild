package com.autobuild.pipeline.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.autobuild.pipeline.configuration.MapperConfig;
import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Stage;
import com.autobuild.pipeline.testutility.DummyData;

@SpringJUnitConfig
@ContextConfiguration(classes = {MapperConfig.class,StageMapper.class})
public class StageMapperTest {
    
    private Stage stage = DummyData.getStage();
    private StageDTO dto = DummyData.getStageDTO();

    @Autowired
    private StageMapper stageMapper;

    @Test
    public void givenStageEntityGetStageDTO() {

        StageDTO convertedStageDTO = stageMapper.entityToDto(stage);
        
        assertDto(convertedStageDTO);
    }

    @Test
    public void givenNullEntityGetStageDTO() {
        assertThrows(IllegalArgumentException.class, () -> stageMapper.entityToDto(null));
    }

    @Test
    public void givenStageDTOGetStageEntity() {
        Stage convertedStage = stageMapper.dtoToEntity(dto);

        assertEntity(convertedStage);
    }
    
    @Test
    public void givenNullDTOGetStageEntity() {
        assertThrows(IllegalArgumentException.class, () -> stageMapper.dtoToEntity(null));
    }

    @Test
    public void multipleMapInvocationTest() {
        Stage s1 = stageMapper.dtoToEntity(dto);
        assertEntity(s1);

        Stage s2 = stageMapper.dtoToEntity(dto);
        assertEntity(s2);
    }

    private void assertEntity(Stage convertedStageEntity) {
        assertEquals(dto.getId(), convertedStageEntity.getId());
        assertEquals(dto.getName(), convertedStageEntity.getName());
        assertEquals(dto.getScriptType(), convertedStageEntity.getScriptType());
    } 

    private void assertDto(StageDTO convertedStageDTO) {
        assertEquals(stage.getId(), convertedStageDTO.getId());
        assertEquals(stage.getName(), convertedStageDTO.getName());
        assertEquals(stage.getScriptType(), convertedStageDTO.getScriptType());
    }
}
