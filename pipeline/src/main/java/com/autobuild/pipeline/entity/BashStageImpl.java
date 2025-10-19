package com.autobuild.pipeline.entity;

import java.nio.file.Path;
import java.util.UUID;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
public class BashStageImpl extends Stage{
    @Getter
    private String command;
}
