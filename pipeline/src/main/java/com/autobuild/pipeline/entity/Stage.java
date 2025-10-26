package com.autobuild.pipeline.entity;

import java.util.UUID;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stage Entity Definition.
 * 
 * @author Suvabrata Chowdhury
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Checks({
    @Check(name = "CHK_STAGE_NAME", constraints = "name != '' "),
    @Check(name = "CHK_STAGE_SCRIPT_TYPE", constraints = "script_type != '' "),
    @Check(name = "CHK_STAGE_PATH", constraints = "path != '' "),
    
    // TODO: remove it as it should be stored as a file later
    @Check(name = "CHK_STAGE_COMMAND", constraints = "command != '' ")
})
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @NotEmpty
    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @NotEmpty
    @Setter
    @Column(name = "script_type", nullable = false)
    private String scriptType;

    //TODO: Check if Path needs to be stored as we are following the storage format /pipelineId/stageId
    @NotEmpty
    @Setter
    @Column(name = "path", nullable = false)
    private String path; // Created script's path

    @NotEmpty
    @Setter
    @Column(name = "command", nullable = false)
    private String command; // TODO: remove it as it should be stored as a file later
}
