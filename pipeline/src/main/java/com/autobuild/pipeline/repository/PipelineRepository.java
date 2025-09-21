package com.autobuild.pipeline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autobuild.pipeline.dto.Pipeline;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, String> {

}
