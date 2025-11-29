package com.autobuild.pipeline.utility.file;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.autobuild.pipeline.definiton.dto.PipelineDTO;
import com.autobuild.pipeline.definiton.dto.StageDTO;
import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.definiton.entity.Stage;

/**
 * General Interface for any file related operations for pipeline.
 * 
 * @author Suvabrata Chowdhury
 */
public interface PipelineFileService {
    public Map<UUID, String> readScriptFiles(final Pipeline pipeline) throws IOException;
    public Map<UUID, String> createScriptFiles(final PipelineDTO pipeline) throws IOException;
    public void removeScriptFiles(final PipelineDTO pipeline) throws IOException;
    public void removeScriptFiles(final Pipeline pipeline) throws IOException;

    public String createStageScriptFile(final Pipeline pipeline, final StageDTO stage) throws IOException;
    public void updateStageScriptFile(final Stage stage, final String command) throws IOException;
    public void removeStageScriptFile(final Stage stage) throws IOException;
    String readStageScriptFile(final Stage stage) throws IOException;

    public String createLogFile(final UUID pipelineBuildId, final UUID stageBuildId) throws IOException;
}
