package com.autobuild.pipeline.definiton.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autobuild.pipeline.definiton.entity.Pipeline;

/**
 * Repository Layer for all pipeline related entities.
 * @author Suvabrata Chowdhury
 */

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {

    // Optional<Pipeline> getPipelineById(String id);
}
