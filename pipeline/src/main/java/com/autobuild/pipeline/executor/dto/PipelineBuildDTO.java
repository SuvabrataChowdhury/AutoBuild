package com.autobuild.pipeline.executor.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PipelineBuildDTO {
    private UUID id;
    private int buildNo;
    
    private List<StageBuildDTO> stageBuildDTOs;
}
