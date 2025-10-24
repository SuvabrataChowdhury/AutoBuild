package com.autobuild.pipeline.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Stage.
 * @author Suvabrata Chowdhury
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StageDTO {
    private UUID id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String scriptType;

    @NotEmpty
    private String command;
}
