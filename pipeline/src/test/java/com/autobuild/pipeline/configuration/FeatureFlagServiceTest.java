package com.autobuild.pipeline.configuration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

public class FeatureFlagServiceTest {

    @TempDir
    Path tempDir;

    private FeatureFlagService serviceWithFile(String json) throws IOException {
        Path file = tempDir.resolve("feature-flags.json");
        Files.writeString(file, json);
        FeatureFlagService service = new FeatureFlagService();
        ReflectionTestUtils.setField(service, "flagsFilePath", file.toString());
        return service;
    }

    @Test
    void testFlagTrueReturnsTrue() throws IOException {
        FeatureFlagService service = serviceWithFile("{\"MY_FLAG\": true}");
        assertTrue(service.getBooleanValue("MY_FLAG", false));
    }

    @Test
    void testFlagFalseReturnsFalse() throws IOException {
        FeatureFlagService service = serviceWithFile("{\"MY_FLAG\": false}");
        assertFalse(service.getBooleanValue("MY_FLAG", true));
    }

    @Test
    void testMissingKeyReturnsDefault() throws IOException {
        FeatureFlagService service = serviceWithFile("{}");
        assertTrue(service.getBooleanValue("MISSING_FLAG", true));
        assertFalse(service.getBooleanValue("MISSING_FLAG", false));
    }

    @Test
    void testFileNotFoundReturnsDefault() {
        FeatureFlagService service = new FeatureFlagService();
        ReflectionTestUtils.setField(service, "flagsFilePath", "/nonexistent/path/flags.json");
        assertTrue(service.getBooleanValue("ANY_FLAG", true));
        assertFalse(service.getBooleanValue("ANY_FLAG", false));
    }
}
