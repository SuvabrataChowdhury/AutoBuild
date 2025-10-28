package com.autobuild.pipeline.utility.file;

import java.io.IOException;

import com.autobuild.pipeline.entity.Pipeline;

/**
 * General Interface for any file related operations for pipeline.
 * 
 * @author Suvabrata Chowdhury
 */
public interface PipelineFileService {
    //TODO: Too low level calls. Make more abstractions
    public void createScriptFiles(final Pipeline pipeline) throws IOException;
    public void removeScriptFiles(final Pipeline pipeline) throws IOException;
}
