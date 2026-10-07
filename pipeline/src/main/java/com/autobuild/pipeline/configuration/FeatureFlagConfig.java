package com.autobuild.pipeline.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.openfeature.contrib.providers.flagd.FlagdOptions;
import dev.openfeature.contrib.providers.flagd.FlagdProvider;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;

/** Wires the OpenFeature flagd provider and exposes a {@link FeatureFlagService} bean. */
@Configuration
public class FeatureFlagConfig {

    @Bean
    public FeatureFlagService featureFlagService(
            @Value("${feature-flags.flagd.host}") String host,
            @Value("${feature-flags.flagd.port}") int port) {
        OpenFeatureAPI api = OpenFeatureAPI.getInstance();
        api.setProvider(new FlagdProvider(FlagdOptions.builder().host(host).port(port).build()));
        Client client = api.getClient();
        return (flagKey, defaultValue) -> client.getBooleanValue(flagKey, defaultValue);
    }
}
