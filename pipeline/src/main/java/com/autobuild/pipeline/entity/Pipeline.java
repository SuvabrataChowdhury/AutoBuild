package com.autobuild.pipeline.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pipeline Entity definition.
 * @author Suvabrata Chowdhury
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_pipeline_name",
            columnNames = {"name"}
            )
    }
)
public class Pipeline {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    private String name; //Should be unique

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "pipeline_stages",
        joinColumns = @JoinColumn(name = "pipeline_id"),
        inverseJoinColumns = @JoinColumn(name = "stage_name")
    )
    @Setter
    private List<Stage> stages; //TODO: need to take an abstract implementation of stage
}
