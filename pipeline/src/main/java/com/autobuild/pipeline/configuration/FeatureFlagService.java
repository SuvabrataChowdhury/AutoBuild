package com.autobuild.pipeline.configuration;

/** Service interface for evaluating feature flags. */
public interface FeatureFlagService {
    boolean getBooleanValue(String flagKey, boolean defaultValue);
}
