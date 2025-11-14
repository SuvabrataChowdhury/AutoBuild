package com.autobuild.pipeline.definiton.dto;

import java.util.List;

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <b>Temporary Solution</b>: Used for any CRUD response on Pipeline entity.
 * @author Suvabrata Chowdhury
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
public class PipelineResponse {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Getter
    private Pipeline pipeline;
    
    @Getter
    @Setter
    private List<String> errors;

    public PipelineResponse(Pipeline pipeline) {
        this.pipeline = pipeline;
    }
}
