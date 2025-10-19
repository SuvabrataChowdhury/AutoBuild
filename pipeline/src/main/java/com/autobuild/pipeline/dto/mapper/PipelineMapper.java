package com.autobuild.pipeline.dto.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.entity.Pipeline;

/**
 * Used to map Pipeline Entity to Pipeline DTO.
 * @author Suvabrata Chowdhury
 */
public class PipelineMapper implements Mapper<PipelineDTO, Pipeline>{
    @Autowired
    private ModelMapper mapper;

    // @Autowired
    // private StageMapper stageMapper;

    @Override
    public Pipeline dtoToEntity(PipelineDTO pipelineDTO) {
        if (null == pipelineDTO) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        TypeMap<PipelineDTO, Pipeline> typeMapper = this.mapper.createTypeMap(PipelineDTO.class, Pipeline.class);
        typeMapper.addMapping(PipelineDTO::getName, Pipeline::setName);

        return this.mapper.map(pipelineDTO, Pipeline.class);
    }

    @Override
    public PipelineDTO entityToDto(Pipeline pipeline) {
        if (null == pipeline) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        TypeMap<Pipeline, PipelineDTO> typeMapper = this.mapper.createTypeMap(Pipeline.class, PipelineDTO.class);
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

        return this.mapper.map(pipeline, PipelineDTO.class);
    }
}
