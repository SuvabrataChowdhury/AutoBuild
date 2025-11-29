package com.autobuild.pipeline.executor.entity;

import java.util.UUID;

import com.autobuild.pipeline.definiton.entity.Stage;
import com.autobuild.pipeline.executor.execution.state.StageExecutionState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class StageBuild {
    @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "stage_id", referencedColumnName = "id", nullable = false)
    private Stage stage;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_build_current_state", nullable = false)
    private StageExecutionState currentState;

    //TODO: Add this attribute
    @Column(name = "log_path", nullable = false)
    private String logPath; //Points to stored log file for reading purpose

    public StageBuild(Stage stage) {
        this.stage = stage;
    }

    @PrePersist
    public void setDefaultState() {
        if (null == this.currentState) {
            this.currentState = StageExecutionState.WAITING;
        }
    }
}