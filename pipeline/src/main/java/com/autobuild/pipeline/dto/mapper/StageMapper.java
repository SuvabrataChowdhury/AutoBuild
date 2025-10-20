package com.autobuild.pipeline.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Stage;

/**
 * Used to map Stage Entity to Stage DTO.
 * @author Suvabrata Chowdhury
 */

@Component
public class StageMapper implements Mapper<StageDTO, Stage>{
    @Autowired
    private ModelMapper mapper;

    @Override
    public Stage dtoToEntity(StageDTO stageDto) {
        if (null == stageDto) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        return this.mapper.map(stageDto, Stage.class);
    }

    @Override
    public StageDTO entityToDto(Stage stage) {
        if (null == stage) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        return this.mapper.map(stage, StageDTO.class);
    }
}
