package com.autobuild.pipeline.utility.file.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.autobuild.pipeline.entity.Pipeline;
import com.autobuild.pipeline.testutility.DummyData;
import com.autobuild.pipeline.utility.file.PipelineFileService;


public class LocalPipelineFileServiceImplTest {
    private Pipeline pipeline = DummyData.pipeline;

    private Path pipelinePath = Path.of("..","pipeline_scripts",pipeline.getId().toString());

    private MockedStatic<Files> filesMockedStatic;

    private PipelineFileService pipelineFileService = new LocalPipelineFileServiceImpl();

    @BeforeEach
    public void setUp() {
        filesMockedStatic = mockStatic(Files.class);

        filesMockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(false);
        filesMockedStatic.when(() -> Files.createDirectory(any(Path.class), any(FileAttribute.class))).thenReturn(pipelinePath);
        filesMockedStatic.when(() -> Files.createDirectories(any(Path.class), any(FileAttribute.class))).thenReturn(pipelinePath);
        filesMockedStatic.when(() -> Files.createFile(any(Path.class), any(FileAttribute.class))).thenReturn(pipelinePath);
    }

    @AfterEach
    public void tearDown() {
        filesMockedStatic.close();
    }

    @Test
    public void testCreateScriptFiles() throws IOException {
        pipelineFileService.createScriptFiles(pipeline);

        filesMockedStatic.verify(() -> Files.createFile(any(Path.class),any(FileAttribute.class)),times(pipeline.getStages().size()));
        filesMockedStatic.verify(() -> Files.write(any(Path.class),any(byte[].class)),times(pipeline.getStages().size()));
        filesMockedStatic.verify(() -> Files.createDirectories(any(Path.class),any(FileAttribute.class)),times(2));
    }

    @Test
    public void testRemoveScriptFiles() throws IOException {
        Stream<Path> paths = Stream.of(pipelinePath.resolve(pipeline.getStages().get(0).getId().toString()), pipelinePath);

        filesMockedStatic.when(() -> Files.walk(any(Path.class))).thenReturn(paths);

        pipelineFileService.removeScriptFiles(pipeline);

        filesMockedStatic.verify(() -> Files.delete(any(Path.class)),times(pipeline.getStages().size() + 1));
    }
}
