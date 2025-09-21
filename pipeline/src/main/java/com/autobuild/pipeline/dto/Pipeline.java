package com.autobuild.pipeline.dto;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pipeline {
    @Getter
    @Id
    private String name;

    @Getter
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "pipeline_stages",
        joinColumns = @JoinColumn(name = "pipeline_name"),
        inverseJoinColumns = @JoinColumn(name = "stage_name")
    )
    private List<BashStageImpl> stages; //TODO: need to take an abstract implementation of stage
}
