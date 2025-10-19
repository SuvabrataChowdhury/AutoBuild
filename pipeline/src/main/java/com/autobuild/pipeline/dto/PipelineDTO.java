package com.autobuild.pipeline.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Pipeline Entity.
 * @author Suvabrata Chowdhury
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PipelineDTO {
    private UUID id;
    private String name;
    private List<StageDTO> stages;
}
