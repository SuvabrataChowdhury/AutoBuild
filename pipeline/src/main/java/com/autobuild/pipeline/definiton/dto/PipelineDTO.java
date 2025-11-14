package com.autobuild.pipeline.definiton.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private String name;

    // @NotNull
    @NotEmpty
    @Valid
    private List<StageDTO> stages;
}
