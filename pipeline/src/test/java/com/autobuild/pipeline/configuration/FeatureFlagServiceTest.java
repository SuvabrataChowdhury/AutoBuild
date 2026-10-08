package com.autobuild.pipeline.configuration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import dev.openfeature.sdk.Client;

public class FeatureFlagServiceTest {

    private FeatureFlagService serviceWith(Client client) {
        return (flagKey, defaultValue) -> client.getBooleanValue(flagKey, defaultValue);
    }

    @Test
    void getBooleanValue_returnsTrue_whenClientReturnsTrue() {
        Client client = mock(Client.class);
        when(client.getBooleanValue("MY_FLAG", false)).thenReturn(true);
        assertTrue(serviceWith(client).getBooleanValue("MY_FLAG", false));
    }

    @Test
    void getBooleanValue_returnsFalse_whenClientReturnsFalse() {
        Client client = mock(Client.class);
        when(client.getBooleanValue("MY_FLAG", true)).thenReturn(false);
        assertFalse(serviceWith(client).getBooleanValue("MY_FLAG", true));
    }

    @Test
    void getBooleanValue_returnsDefault_whenClientReturnsDefault() {
        Client client = mock(Client.class);
        when(client.getBooleanValue("MISSING_FLAG", true)).thenReturn(true);
        when(client.getBooleanValue("MISSING_FLAG", false)).thenReturn(false);
        assertTrue(serviceWith(client).getBooleanValue("MISSING_FLAG", true));
        assertFalse(serviceWith(client).getBooleanValue("MISSING_FLAG", false));
    }
}
