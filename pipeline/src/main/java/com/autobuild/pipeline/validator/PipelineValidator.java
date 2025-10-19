package com.autobuild.pipeline.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.entity.Stage;

/**
 * Validator for pipeline requests.
 * 
 * @author Suvabrata Chowdhury
 */

//TODO: Improve design
@Component
public class PipelineValidator implements Validator{

    public Errors validatePipeline(final Pipeline pipeline) {
        Errors validationErrors = new BeanPropertyBindingResult(pipeline, "pipeline");
        this.validate(pipeline, validationErrors);

        return validationErrors;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Pipeline.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Pipeline pipeline = (Pipeline) target;

        if (containsDuplicateStageName(pipeline.getStages())) {
            errors.rejectValue("stages", "stages.duplicate" , "stages having duplicate name");
        }
    }

    //TODO: Check if this can be enforced in db level
    private boolean containsDuplicateStageName(List<Stage> stages) {
        Set<String> stageNameTable = new HashSet<>();

        for (Stage stage: stages) {
            if (stageNameTable.contains(stage.getName())) {
                return true;
            }

            stageNameTable.add(stage.getName());
        }

        return false;
    }
}
