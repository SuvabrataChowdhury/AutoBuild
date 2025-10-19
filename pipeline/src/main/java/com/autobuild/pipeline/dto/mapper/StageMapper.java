package com.autobuild.pipeline.dto.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
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

        TypeMap<StageDTO, Stage> typeMapper = this.mapper.createTypeMap(StageDTO.class, Stage.class);
        typeMapper.addMapping(StageDTO::getName, Stage::setName);
        typeMapper.addMapping(StageDTO::getScriptType, Stage::setScriptType);
        typeMapper.addMapping(StageDTO::getCommand, Stage::setCommand);

        return this.mapper.map(stageDto, Stage.class);
    }

    @Override
    public StageDTO entityToDto(Stage stage) {
        if (null == stage) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        TypeMap<Stage, StageDTO> typeMapper = this.mapper.createTypeMap(Stage.class, StageDTO.class);
        typeMapper.addMapping(Stage::getId, StageDTO::setId);
        typeMapper.addMapping(Stage::getName, StageDTO::setName);
        typeMapper.addMapping(Stage::getScriptType, StageDTO::setScriptType);
        typeMapper.addMapping(Stage::getCommand, StageDTO::setCommand);

        return this.mapper.map(stage, StageDTO.class);
    }
}
