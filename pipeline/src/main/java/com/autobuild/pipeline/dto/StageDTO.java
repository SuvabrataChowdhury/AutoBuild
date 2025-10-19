package com.autobuild.pipeline.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StageDTO {
    private UUID id;
    private String name;
    private String scriptType;
    private String command;
}
