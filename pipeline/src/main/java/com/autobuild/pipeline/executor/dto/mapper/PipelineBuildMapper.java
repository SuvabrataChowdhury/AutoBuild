package com.autobuild.pipeline.executor.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.definiton.dto.mapper.Mapper;
import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.entity.PipelineBuild;

/**
 * Used to map PipelineBuild to PipelineBuildDTO and vice versa.
 * 
 * @author Suvabrata Chowdhury
 */

@Component
public class PipelineBuildMapper implements Mapper<PipelineBuildDTO, PipelineBuild> {

    @Autowired
    private ModelMapper mapper;

    @Override
    public PipelineBuild dtoToEntity(PipelineBuildDTO pipelineBuildDTO) {
        throw new UnsupportedOperationException("DTO is not persistable");
    }

    @Override
    public PipelineBuildDTO entityToDto(PipelineBuild pipelineBuild) {
        if (null == pipelineBuild) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        return this.mapper.map(pipelineBuild, PipelineBuildDTO.class);
    }
}
