package com.autobuild.pipeline.testutility;

import java.util.List;
import java.util.UUID;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.entity.Stage;

public class DummyData {

    public static Stage getStage() {
        return new Stage(UUID.randomUUID(),"dummyStage1","bash","./dummyFolder/");
    }

    public static  StageDTO getStageDTO() {
        return new StageDTO(UUID.randomUUID(), "dummyStage1", "bash", "echo Hello");
    }

    public static  Pipeline getPipeline() {
        return new Pipeline(UUID.randomUUID(), "my pipeline",List.of(getStage()));
    }

    public static PipelineDTO getPipelineDTO() {
        return new PipelineDTO(UUID.randomUUID(), "my pipeline", List.of(getStageDTO()));
    }
}