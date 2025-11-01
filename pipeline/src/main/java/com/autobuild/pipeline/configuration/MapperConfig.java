package com.autobuild.pipeline.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.entity.Stage;

/**
 * Bean Configurations for the application.
 * @author Suvabrata Chowdhury
 */

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper mapper = new ModelMapper();

        setStageMappings(mapper);
        setPipelineMapping(mapper);

        return mapper;
    }

    private void setStageMappings(ModelMapper mapper) {
        setStageDTOEntityMapping(mapper);
        setStageEntityDTOMapping(mapper);
    }

    private void setStageDTOEntityMapping(ModelMapper mapper) {
        TypeMap<StageDTO, Stage> typeMapper = mapper.createTypeMap(StageDTO.class, Stage.class);
        typeMapper.addMapping(StageDTO::getId, Stage::setId);
        typeMapper.addMapping(StageDTO::getName, Stage::setName);
        typeMapper.addMapping(StageDTO::getScriptType, Stage::setScriptType);
        // typeMapper.addMapping(StageDTO::getCommand, Stage::setCommand);
    }

    private void setStageEntityDTOMapping(ModelMapper mapper) {
        TypeMap<Stage, StageDTO> typeMapper = mapper.createTypeMap(Stage.class, StageDTO.class);
        typeMapper.addMapping(Stage::getId, StageDTO::setId);
        typeMapper.addMapping(Stage::getName, StageDTO::setName);
        typeMapper.addMapping(Stage::getScriptType, StageDTO::setScriptType);
        // typeMapper.addMapping(Stage::getCommand, StageDTO::setCommand);
    }

    private void setPipelineMapping(ModelMapper mapper) {
        setPipelineDTOEntityMapping(mapper);
        setPipelineEntityDTOMapping(mapper);
    }

    private void setPipelineDTOEntityMapping(ModelMapper mapper) {
        TypeMap<PipelineDTO, Pipeline> typeMapper = mapper.createTypeMap(PipelineDTO.class, Pipeline.class);
        typeMapper.addMapping(PipelineDTO::getId, Pipeline::setId);
        typeMapper.addMapping(PipelineDTO::getName, Pipeline::setName);
    }

    private void setPipelineEntityDTOMapping(ModelMapper mapper) {
        TypeMap<Pipeline, PipelineDTO> typeMapper = mapper.createTypeMap(Pipeline.class, PipelineDTO.class);
        typeMapper.addMapping(Pipeline::getName, PipelineDTO::setName);
        typeMapper.addMapping(Pipeline::getId, PipelineDTO::setId);
        // typeMapper.addMapping(p -> p.getStages().stream().map(s -> {
        //     try {
        //         return stageMapper.entityToDto(s);
        //     } catch (InvalidMappingOperationException e) {
        //         e.printStackTrace();
        //     }

        //     return null;
        // }).collect(Collectors.toList()), PipelineDTO::setStages);
    }
}
