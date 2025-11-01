package com.autobuild.pipeline.utility.file;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.autobuild.pipeline.dto.PipelineDTO;
import com.autobuild.pipeline.entity.Pipeline;

/**
 * General Interface for any file related operations for pipeline.
 * 
 * @author Suvabrata Chowdhury
 */
public interface PipelineFileService {
    public Map<UUID, String> readScriptFiles(final Pipeline pipeline) throws IOException;
    public Map<UUID, String> createScriptFiles(final PipelineDTO pipeline) throws IOException;
    public void removeScriptFiles(final PipelineDTO pipeline) throws IOException; //can also be done with entity
}
