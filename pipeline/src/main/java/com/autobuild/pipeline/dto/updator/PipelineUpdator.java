package com.autobuild.pipeline.dto.updator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.entity.Stage;

import jakarta.persistence.EntityNotFoundException;

@Component
public class PipelineUpdator implements EntityUpdator<PipelineDTO, Pipeline> {

    @Autowired
    private StageUpdator stageUpdator;

    /**
     * get all non-null, non-empty, non-stage & non-id field values and map them in entity.
     */

    //TODO: use reflection to get to a more generic solution as in future more fields might be added to the entity
    //TODO: creation and deletion of stages can not be performed currently so create new stage api to do such stuff. Currently creating and updating stages.

    @Override
    public void update(PipelineDTO dto, Pipeline entity) {

        if(StringUtils.isNotEmpty(dto.getName())) {
            entity.setName(dto.getName());
        }

        List<StageDTO> stageDtosWithId = dto.getStages().stream().filter(stageDto -> Objects.nonNull(stageDto.getId())).toList();
        Map<UUID,Stage> stagesMap = entity.getStages().stream().collect(Collectors.toMap(Stage::getId, stage -> stage));

        //Update Stages
        if (stageDtosWithId != null) {
            stageDtosWithId.forEach(stageDto -> {
                Stage stageToBeUpdated = stagesMap.get(stageDto.getId());

                if (null == stageToBeUpdated) {
                    throw new EntityNotFoundException("Given stage with id " + stageDto.getId() + " not found" );
                }

                stageUpdator.update(stageDto,stageToBeUpdated);
            });
        }
    }
}
