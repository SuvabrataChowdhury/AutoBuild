package com.autobuild.pipeline.dto;

import java.util.UUID;

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
    private String name;
    private String scriptType;
    private String command;
}
