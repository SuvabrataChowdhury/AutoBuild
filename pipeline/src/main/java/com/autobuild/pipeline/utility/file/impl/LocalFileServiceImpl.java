package com.autobuild.pipeline.utility.file.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.autobuild.pipeline.utility.file.FileService;

@Component
public class LocalFileServiceImpl implements FileService {
    private static final String DEFAULT_SCRIPT_PATH = "../pipeline_scripts/";

    @Override
    public void createScriptFile(final String directory, final String scriptName, final String content) throws IOException {
        Path directoryPath = Path.of(DEFAULT_SCRIPT_PATH, directory);
        Path filePath = Path.of(directoryPath.toString(),scriptName);

        if(!Files.exists(directoryPath)) {
            Files.createDirectory(directoryPath, PosixFilePermissions.asFileAttribute(Set.of(PosixFilePermission.OWNER_EXECUTE,PosixFilePermission.OWNER_WRITE,PosixFilePermission.OWNER_READ)));
        }

        Files.createFile(filePath, PosixFilePermissions.asFileAttribute(Set.of(PosixFilePermission.OWNER_EXECUTE,PosixFilePermission.OWNER_WRITE,PosixFilePermission.OWNER_READ)));
        Files.write(filePath, content.getBytes());
    }

    @Override
    public void removeDirectory(final String directory) throws IOException {
        Path directoryPath = Path.of(DEFAULT_SCRIPT_PATH, directory);

        try (Stream<Path> paths = Files.walk(directoryPath)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }
}
