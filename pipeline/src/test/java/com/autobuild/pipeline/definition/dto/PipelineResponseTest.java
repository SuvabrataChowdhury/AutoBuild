package com.autobuild.pipeline.definition.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.autobuild.pipeline.definiton.dto.PipelineResponse;
import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.definiton.entity.Stage;

public class PipelineResponseTest {
    
    @Test
    public void testDefaultConstructor() {
        PipelineResponse response = new PipelineResponse();
        assertNull(response.getPipeline());
        assertNull(response.getErrors());
    }

    @Test
    public void testConstructorWithPipeline() {
        Pipeline pipeline = new Pipeline();
        pipeline.setId(UUID.randomUUID());
        pipeline.setName("test-pipeline");
        
        PipelineResponse response = new PipelineResponse(pipeline);
        
        assertNotNull(response.getPipeline());
        assertEquals(pipeline, response.getPipeline());
        assertEquals("test-pipeline", response.getPipeline().getName());
        assertNull(response.getErrors());
    }

    @Test
    public void testSetAndGetErrors() {
        PipelineResponse response = new PipelineResponse();
        List<String> errors = new ArrayList<>();
        errors.add("Error 1");
        errors.add("Error 2");
        
        response.setErrors(errors);
        
        assertNotNull(response.getErrors());
        assertEquals(2, response.getErrors().size());
        assertTrue(response.getErrors().contains("Error 1"));
        assertTrue(response.getErrors().contains("Error 2"));
    }

    @Test
    public void testPipelineWithStages() {
        Pipeline pipeline = new Pipeline();
        pipeline.setId(UUID.randomUUID());
        pipeline.setName("complex-pipeline");
        
        Stage stage1 = new Stage();
        stage1.setId(UUID.randomUUID());
        stage1.setName("build");
        stage1.setScriptType("bash");
        
        Stage stage2 = new Stage();
        stage2.setId(UUID.randomUUID());
        stage2.setName("test");
        stage2.setScriptType("python");
        
        List<Stage> stages = new ArrayList<>();
        stages.add(stage1);
        stages.add(stage2);
        pipeline.setStages(stages);
        
        PipelineResponse response = new PipelineResponse(pipeline);
        
        assertNotNull(response.getPipeline());
        assertEquals(2, response.getPipeline().getStages().size());
        assertEquals("build", response.getPipeline().getStages().get(0).getName());
        assertEquals("test", response.getPipeline().getStages().get(1).getName());
    }

    @Test
    public void testResponseWithPipelineAndErrors() {
        Pipeline pipeline = new Pipeline();
        pipeline.setId(UUID.randomUUID());
        pipeline.setName("test-pipeline");
        
        PipelineResponse response = new PipelineResponse(pipeline);
        
        List<String> errors = new ArrayList<>();
        errors.add("Warning: Deprecated stage type");
        response.setErrors(errors);
        
        assertNotNull(response.getPipeline());
        assertNotNull(response.getErrors());
        assertEquals("test-pipeline", response.getPipeline().getName());
        assertEquals(1, response.getErrors().size());
    }

    @Test
    public void testEmptyErrorsList() {
        PipelineResponse response = new PipelineResponse();
        response.setErrors(new ArrayList<>());
        
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().isEmpty());
    }

    @Test
    public void testNullPipelineInConstructor() {
        PipelineResponse response = new PipelineResponse(null);
        assertNull(response.getPipeline());
    }
}