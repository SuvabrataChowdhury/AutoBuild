package com.autobuild.pipeline.configuration;

public interface FeatureFlagService {
    boolean getBooleanValue(String flagKey, boolean defaultValue);
}
