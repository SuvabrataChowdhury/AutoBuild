package com.autobuild.pipeline.utility.file;

import java.io.IOException;

public interface FileService {
    //TODO: Too low level calls. Make more abstractions
    public void createScriptFile(final String directory, final String scriptName, final String content) throws IOException;
    public void removeDirectory(final String directory) throws IOException;
}
