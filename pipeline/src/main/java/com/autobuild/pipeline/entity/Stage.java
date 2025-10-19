package com.autobuild.pipeline.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Stage {
    @Getter
    @Id
    @GeneratedValue
    private UUID id;
    private String name; //TODO: Change this primary key. Two stages can have same name
    private String scriptType;
    private String path; //Created script's path
}
