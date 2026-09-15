package com.autobuild.pipeline.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads feature flags from a JSON file on every call (hot-reload — no restart needed).
 * File format: {"FLAG_NAME": true, "OTHER_FLAG": false}
 */
@Service
public class FeatureFlagService {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${feature-flags.file:./flags/feature-flags.json}")
    private String flagsFilePath;

    public boolean getBooleanValue(String flagKey, boolean defaultValue) {
        Map<String, Boolean> flags = readFlags();
        return flags.getOrDefault(flagKey, defaultValue);
    }

    private Map<String, Boolean> readFlags() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(flagsFilePath));
            return objectMapper.readValue(bytes, new TypeReference<Map<String, Boolean>>() { });
        } catch (IOException e) {
            log.warn("Could not read feature flags file at {}: {}", flagsFilePath, e.getMessage());
            return Collections.emptyMap();
        }
    }
}
