package com.autobuild.pipeline.executor.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StageBuildDTO {
    private UUID id;
    // private String buildStatus; //TODO: use enums for strict enforcement

    private UUID stageId;
}