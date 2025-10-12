package com.autobuild.pipeline.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autobuild.pipeline.entity.Pipeline;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {

    // Optional<Pipeline> getPipelineById(String id);
}
