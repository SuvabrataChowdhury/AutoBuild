package com.autobuild.pipeline.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;

@Service
public class FileBasedFeatureFlagService implements FeatureFlagService {

    private final Client client;

    public FileBasedFeatureFlagService(@Value("${feature-flags.file}") String flagsFilePath) {
        OpenFeatureAPI api = OpenFeatureAPI.getInstance();
        api.setProviderAndWait(new JsonFileFeatureProvider(flagsFilePath));
        this.client = api.getClient();
    }

    @Override
    public boolean getBooleanValue(String flagKey, boolean defaultValue) {
        return client.getBooleanValue(flagKey, defaultValue);
    }
}
