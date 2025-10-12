package com.autobuild.pipeline.entity;

import java.util.UUID;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
    @GeneratedValue
    private UUID id;

    @Getter
    private String name; //TODO: Change this primary key. Two stages can have same name

    @Getter
    private String command;
}
