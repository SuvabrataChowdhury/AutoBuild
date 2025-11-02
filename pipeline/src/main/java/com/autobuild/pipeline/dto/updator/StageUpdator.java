package com.autobuild.pipeline.dto.updator;

import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Stage;

import io.micrometer.common.util.StringUtils;

public class StageUpdator implements EntityUpdator<StageDTO, Stage> {

    @Override
    public Stage update(StageDTO stageDTO, Stage stage) {
        if (!StringUtils.isEmpty(stageDTO.getName())) {
            stage.setName(stageDTO.getName());
        }

        if (!StringUtils.isEmpty(stageDTO.getCommand())) {
            
        }
    }

}
