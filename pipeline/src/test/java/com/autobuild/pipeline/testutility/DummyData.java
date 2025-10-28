package com.autobuild.pipeline.testutility;

import java.util.List;
import java.util.UUID;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.entity.Stage;

public class DummyData {
    public static final Stage stage = new Stage(UUID.randomUUID(),"dummyStage1","bash","echo Hello");
    public static final StageDTO stageDto = new StageDTO(UUID.randomUUID(), "dummyStage1", "bash", "echo Hello");

    public static final Pipeline pipeline = new Pipeline(UUID.randomUUID(), "my pipeline",List.of(stage));
    public static final PipelineDTO pipelineDTO = new PipelineDTO(UUID.randomUUID(), "my pipeline", List.of(stageDto));
}