package com.autobuild.pipeline.utility.file.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.dto.StageDTO;
import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.utility.file.PipelineFileService;
import com.autobuild.pipeline.utility.file.extension.Extensions;

/**
 * Pipeline file service implementation for storing script file in local
 * machine.
 * 
 * The maintained directory structure is,
 * /pipeline_id/stage_id
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
    public Map<UUID, String> readScriptFiles(final Pipeline pipeline) throws IOException {
        Map<UUID, String> scriptContents = new HashMap<>();

        try {
            pipeline.getStages()
                    .forEach(
                            stage -> {
                                try {
                                    String content = StringUtils.join(
                                                        Files.readAllLines(Path.of(stage.getPath())),
                                                         "\n"
                                                    );
                                    scriptContents.put(stage.getId(), content);
                                } catch (IOException e) {
                                    throw new UncheckedIOException(e);
                                }
                            });

            return scriptContents;
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @Override
    public Map<UUID, String> createScriptFiles(final PipelineDTO pipeline) throws IOException {
        createParentDirectory();

        Path directoryPath = createPipelineDirectory(pipeline);

        Map<UUID, String> scriptPaths = new HashMap<>();
        for (StageDTO stage : pipeline.getStages()) {
            String path = createStageFile(directoryPath, stage);

            scriptPaths.put(stage.getId(), path);
        }

        return scriptPaths;
    }

    @Override
    public void removeScriptFiles(final PipelineDTO pipeline) throws IOException {
        Path directoryPath = getPipelineDirectoryPath(pipeline.getId().toString());

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

    private Path createPipelineDirectory(PipelineDTO pipeline) throws IOException {
        Path directoryPath = getPipelineDirectoryPath(pipeline.getId().toString());
        directoryPath = Files.createDirectories(directoryPath, PERMISSIONS);

        return directoryPath;
    }

    private String createStageFile(Path directoryPath, StageDTO stage) throws IOException {
        String scriptName = stage.getId() + Extensions.nameFor(stage.getScriptType());
        Path filePath = directoryPath.resolve(scriptName);

        Files.createFile(filePath, PERMISSIONS);
        Files.write(filePath, stage.getCommand().getBytes());

        return filePath.toString();
    }

    private Path getPipelineDirectoryPath(String pipelineId) {
        return DEFAULT_SCRIPT_PATH.resolve(pipelineId);
    }
}
