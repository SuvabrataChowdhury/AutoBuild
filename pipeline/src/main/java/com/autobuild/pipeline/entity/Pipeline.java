package com.autobuild.pipeline.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Check;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pipeline Entity definition.
 * 
 * @author Suvabrata Chowdhury
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UK_PIPELINE_NAME", columnNames = { "name" })
})
@Check(name = "CHK_PIPELINE_NAME", constraints = "name != '' ")
public class Pipeline {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @NotEmpty
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "pipeline_stages", 
        joinColumns = @JoinColumn(name = "pipeline_id"),
        inverseJoinColumns = @JoinColumn(name = "stage_id")
    )
    private List<Stage> stages; // TODO: need to take an abstract implementation of stage
}
