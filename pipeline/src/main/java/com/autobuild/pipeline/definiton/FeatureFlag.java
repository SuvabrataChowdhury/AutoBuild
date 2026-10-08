package com.autobuild.pipeline.definiton;

/** Enum of all feature flag keys used in this application. */
public enum FeatureFlag {
    ENABLE_EDIT_PIPELINE("ENABLE_EDIT_PIPELINE");

    private final String key;

    FeatureFlag(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
