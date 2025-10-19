package com.autobuild.pipeline.entity;

import jakarta.persistence.Entity;
import lombok.Getter;

/**
 * <b>Temporary Solution:</b> Bash Script implementation for stage.
 * @author Suvabrata Chowdhury
 */

// @Entity
public class BashStageImpl{
    @Getter
    private String command;
}
