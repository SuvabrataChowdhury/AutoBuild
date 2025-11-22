package com.autobuild.pipeline.executor.entity;

import java.util.UUID;

import com.autobuild.pipeline.definiton.entity.Stage;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * StageBuild entity for holding stage execution metadata/data in table.
 * 
 * @author Suvabrata Chowdhury
 */

//TODO: Add timestamps
//TODO: Add status like BUILDING, SUCCESS, FAILURE etc.
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class StageBuild {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "stage_id", referencedColumnName = "id", nullable = false)
    private Stage stage;

    //TODO: Add this attribute
    // @Column(name = "build_status", nullable = false)
    // private String buildStatus; //TODO: use Enums for strict enforcement

    //TODO: Add this attribute
    // @Column(name = "log_path", nullable = false)
    // private String logPath; //Points to stored log file for reading purpose

    public StageBuild(Stage stage) {
        this.stage = stage;
    }
}