package com.autobuild.pipeline.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;

/**
 * OpenFeature provider that reads boolean flags from a simple JSON file.
 * File format: {"FLAG_NAME": true, "OTHER_FLAG": false}
 * Hot-reloads on every evaluation — no restart needed.
 */
public class JsonFileFeatureProvider implements FeatureProvider {

    private static final Logger log = LoggerFactory.getLogger(JsonFileFeatureProvider.class);
    private static final String NAME = "json-file-provider";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String flagsFilePath;

    public JsonFileFeatureProvider(String flagsFilePath) {
        this.flagsFilePath = flagsFilePath;
    }

    @Override
    public Metadata getMetadata() {
        return () -> NAME;
    }

    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(String key, Boolean defaultValue, EvaluationContext ctx) {
        Boolean value = readFlags().get(key);
        if (value == null) {
            return ProviderEvaluation.<Boolean>builder()
                    .value(defaultValue)
                    .reason(Reason.DEFAULT.toString())
                    .build();
        }
        return ProviderEvaluation.<Boolean>builder()
                .value(value)
                .reason(Reason.STATIC.toString())
                .build();
    }

    @Override
    public ProviderEvaluation<String> getStringEvaluation(String key, String defaultValue, EvaluationContext ctx) {
        return ProviderEvaluation.<String>builder().value(defaultValue).reason(Reason.DEFAULT.toString()).build();
    }

    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(String key, Integer defaultValue, EvaluationContext ctx) {
        return ProviderEvaluation.<Integer>builder().value(defaultValue).reason(Reason.DEFAULT.toString()).build();
    }

    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(String key, Double defaultValue, EvaluationContext ctx) {
        return ProviderEvaluation.<Double>builder().value(defaultValue).reason(Reason.DEFAULT.toString()).build();
    }

    @Override
    public ProviderEvaluation<Value> getObjectEvaluation(String key, Value defaultValue, EvaluationContext ctx) {
        return ProviderEvaluation.<Value>builder().value(defaultValue).reason(Reason.DEFAULT.toString()).build();
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
