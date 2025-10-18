package com.autobuild.pipeline.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PipelineDTO {
    private String name;
    private List<StageDTO> stages;
}
