package com.autobuild.pipeline.testutility;

import java.util.List;
import java.util.UUID;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.entity.Stage;

import lombok.Getter;

public class DummyData {
    // private static final Stage stage = new Stage(UUID.randomUUID(),"dummyStage1","bash","echo Hello");
    // private static final StageDTO stageDto = new StageDTO(UUID.randomUUID(), "dummyStage1", "bash", "echo Hello");

    // private static final Pipeline pipeline = new Pipeline(UUID.randomUUID(), "my pipeline",List.of(stage));
    // private static final PipelineDTO pipelineDTO = new PipelineDTO(UUID.randomUUID(), "my pipeline", List.of(stageDto));

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