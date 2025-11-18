package com.autobuild.pipeline.executor.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for StageBuild.
 * 
 * @author Suvabrata Chowdhury
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StageBuildDTO {
    private UUID id;
    // private String buildStatus; //TODO: use enums for strict enforcement

    private UUID stageId;
}