package com.autobuild.pipeline.utility.file.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.entity.Stage;
import com.autobuild.pipeline.utility.file.PipelineFileService;
import com.autobuild.pipeline.utility.file.extension.Extensions;

/**
 * Pipeline file service implementation for storing script file in local
 * machine.
 * 
 * @author Suvabrata Chowdhury
 */

@Component
public class LocalPipelineFileServiceImpl implements PipelineFileService {
    private static final Path DEFAULT_SCRIPT_PATH = Path.of("..", "pipeline_scripts");
    private static final FileAttribute<Set<PosixFilePermission>> PERMISSIONS = PosixFilePermissions.asFileAttribute(
            Set.of(
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_READ));

    @Override
    public void createScriptFiles(final Pipeline pipeline) throws IOException {
        createParentDirectory();

        Path directoryPath = createPipelineDirectory(pipeline);

        for (Stage stage : pipeline.getStages()) {
            createStageFile(directoryPath, stage);
        }
    }

    @Override
    public void removeScriptFiles(final Pipeline pipeline) throws IOException {
        Path directoryPath = getPipelineDirectoryPath(pipeline);

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

    private void createParentDirectory() throws IOException {
        if (!Files.exists(DEFAULT_SCRIPT_PATH)) {
            Files.createDirectories(DEFAULT_SCRIPT_PATH, PERMISSIONS);
        }
    }

    private Path createPipelineDirectory(Pipeline pipeline) throws IOException {
        Path directoryPath = getPipelineDirectoryPath(pipeline);
        directoryPath = Files.createDirectories(directoryPath, PERMISSIONS);

        return directoryPath;
    }

    private void createStageFile(Path directoryPath, Stage stage) throws IOException {
        String scriptName = stage.getId() + Extensions.nameFor(stage.getScriptType());
        Path filePath = directoryPath.resolve(scriptName);

        Files.createFile(filePath, PERMISSIONS);
        Files.write(filePath, stage.getCommand().getBytes());
    }


    private Path getPipelineDirectoryPath(Pipeline pipeline) {
        return DEFAULT_SCRIPT_PATH.resolve(pipeline.getId().toString());
    }
}
