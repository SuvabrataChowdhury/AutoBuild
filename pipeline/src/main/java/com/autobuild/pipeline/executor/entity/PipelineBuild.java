package com.autobuild.pipeline.executor.entity;

import java.util.List;
import java.util.UUID;

import com.autobuild.pipeline.definiton.entity.Pipeline;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PipelineBuild entity to hold data/metadata about execution in table.
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
//TODO: Add it back with build number
// @Table(
//     uniqueConstraints = {
//         @UniqueConstraint(name = "UK_PIPELINEBUILD_BUILDNO", columnNames = { "build_number" })
//     })
public class PipelineBuild {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //TODO: add build number to indicate how many times the specific pipeline has run
    // @Column(name = "build_number", nullable = false)
    // private int buildNo;

    @ManyToOne
    @JoinColumn(name = "pipeline_id", referencedColumnName = "id", nullable = false)
    private Pipeline pipeline;

    @NotEmpty
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "pipeline_stage_builds",
        joinColumns = @JoinColumn(name = "pipeline_build_id"),
        inverseJoinColumns = @JoinColumn(name = "stage_build_id")
    )
    private List<StageBuild> stageBuilds;

    public PipelineBuild(Pipeline pipeline, List<StageBuild> stageBuilds) {
        this.pipeline = pipeline;
        this.stageBuilds = stageBuilds;
    }
}