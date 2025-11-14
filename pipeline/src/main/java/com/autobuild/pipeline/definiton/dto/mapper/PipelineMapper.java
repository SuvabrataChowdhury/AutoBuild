package com.autobuild.pipeline.definiton.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.definiton.dto.PipelineDTO;
import com.autobuild.pipeline.definiton.entity.Pipeline;

/**
 * Used to map Pipeline Entity to Pipeline DTO.
 * @author Suvabrata Chowdhury
 */

@Component
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

        return this.mapper.map(pipelineDTO, Pipeline.class);
    }

    @Override
    public PipelineDTO entityToDto(Pipeline pipeline) {
        if (null == pipeline) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        return this.mapper.map(pipeline, PipelineDTO.class);
    }
}
