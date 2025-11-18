package com.autobuild.pipeline.executor.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.definiton.dto.mapper.Mapper;
import com.autobuild.pipeline.definiton.entity.Stage;
import com.autobuild.pipeline.executor.dto.StageBuildDTO;
import com.autobuild.pipeline.executor.entity.StageBuild;

/**
 * Used to map StageBuild to StageBuildDTO and vice versa.
 * 
 * @author Suvabrata Chowdhury
 */

@Component
public class StageBuildMapper implements Mapper<StageBuildDTO, StageBuild>{

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StageBuild dtoToEntity(StageBuildDTO stageBuildDTO) {
        if (null == stageBuildDTO) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        StageBuild stageBuild = modelMapper.map(stageBuildDTO, StageBuild.class);

        //TODO: make sure it contains actual stage info
        Stage dummyStage = new Stage();
        dummyStage.setId(stageBuildDTO.getStageId());

        stageBuild.setStage(dummyStage);

        return stageBuild;
    }

    @Override
    public StageBuildDTO entityToDto(StageBuild stageBuild) {
        if (null == stageBuild) {
            throw new IllegalArgumentException("Given null object can not be mapped");
        }

        return this.modelMapper.map(stageBuild, StageBuildDTO.class);
    }
    
}
