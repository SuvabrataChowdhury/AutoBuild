package com.autobuild.pipeline.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.openfeature.contrib.providers.ofrep.OfrepProvider;
import dev.openfeature.contrib.providers.ofrep.OfrepProviderOptions;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;

/** Wires an OpenFeature provider and exposes a {@link FeatureFlagService} bean. */
@Configuration
@EnableConfigurationProperties(FeatureFlagConfig.LocalOverrideProperties.class)
public class FeatureFlagConfig {

    @ConfigurationProperties(prefix = "feature-flags")
    static class LocalOverrideProperties {
        private Map<String, Boolean> localOverrides = new HashMap<>();

        public Map<String, Boolean> getLocalOverrides() {
            return localOverrides;
        }

        public void setLocalOverrides(Map<String, Boolean> localOverrides) {
            this.localOverrides = localOverrides;
        }
    }

    @Bean
    @ConditionalOnProperty("feature-flags.provider.uri")
    public FeatureFlagService openFeatureFlagService(
            @Value("${feature-flags.provider.uri}") String uri) throws Exception {
        OpenFeatureAPI api = OpenFeatureAPI.getInstance();
        api.setProviderAndWait(OfrepProvider.constructProvider(OfrepProviderOptions.builder().baseUrl(uri).build()));
        Client client = api.getClient();
        return (flagKey, defaultValue) -> client.getBooleanValue(flagKey, defaultValue);
    }

    @Bean
    @ConditionalOnMissingBean
    public FeatureFlagService noOpFeatureFlagService(LocalOverrideProperties props) {
        return (flagKey, defaultValue) -> props.getLocalOverrides().getOrDefault(flagKey, defaultValue);
    }
}
