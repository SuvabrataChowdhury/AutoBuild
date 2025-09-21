package com.autobuild.pipeline.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BashStageImpl{

    @Getter
    @Id
    private String name;

    @Getter
    private String command;
}
