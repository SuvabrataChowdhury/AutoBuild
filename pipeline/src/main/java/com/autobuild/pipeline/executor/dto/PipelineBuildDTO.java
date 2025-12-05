package com.autobuild.pipeline.executor.dto;

import java.util.List;
import java.util.UUID;

import com.autobuild.pipeline.executor.execution.state.PipelineExecutionState;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for PipelineBuild.
 * 
 * @author Suvabrata Chowdhury
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PipelineBuildDTO {
    private UUID id;
    // private int buildNo;

    private UUID pipelineId;

    private String pipelineName;

    private PipelineExecutionState currentState;
    
    private List<StageBuildDTO> stageBuilds;
}
