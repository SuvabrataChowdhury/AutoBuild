package com.autobuild.pipeline.executor.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.definiton.dto.mapper.Mapper;
import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.entity.PipelineBuild;

@Component
public class PipelineBuildMapper implements Mapper<PipelineBuildDTO,PipelineBuild> {

    @Autowired
    private ModelMapper mapper;

    @Override
    public PipelineBuild dtoToEntity(PipelineBuildDTO pipelineBuildDTO) {
        if(null == pipelineBuildDTO) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        PipelineBuild pipelineBuild = this.mapper.map(pipelineBuildDTO, PipelineBuild.class);

        //TODO: make sure it contains actual stage info
        Pipeline dummyPipeline = new Pipeline();
        dummyPipeline.setId(pipelineBuildDTO.getPipelineId());

        pipelineBuild.setPipeline(dummyPipeline);

        return pipelineBuild;
    }

    @Override
    public PipelineBuildDTO entityToDto(PipelineBuild pipelineBuild) {
        if( null == pipelineBuild ) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        return this.mapper.map(pipelineBuild, PipelineBuildDTO.class);
    }
}
