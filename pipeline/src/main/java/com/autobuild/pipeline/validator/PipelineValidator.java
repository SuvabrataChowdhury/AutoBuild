package com.autobuild.pipeline.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.StageDTO;

/**
 * Validator for pipeline requests.
 * 
 * @author Suvabrata Chowdhury
 */

//TODO: Improve design
@Component
public class PipelineValidator implements Validator{

    public Errors validatePipeline(final PipelineDTO pipeline) {
        Errors validationErrors = new BeanPropertyBindingResult(pipeline, "pipeline");
        this.validate(pipeline, validationErrors);

        return validationErrors;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PipelineDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PipelineDTO pipeline = (PipelineDTO) target;

        if (containsDuplicateStageName(pipeline.getStages())) {
            errors.rejectValue("stages", "stages.duplicate" , "Stages having duplicate name");
        }
    }

    //TODO: Check if this can be enforced in db level
    private boolean containsDuplicateStageName(List<StageDTO> stages) {
        Set<String> stageNameTable = new HashSet<>();

        for (StageDTO stage: stages) {
            if (stageNameTable.contains(stage.getName())) {
                return true;
            }

            stageNameTable.add(stage.getName());
        }

        return false;
    }
}
