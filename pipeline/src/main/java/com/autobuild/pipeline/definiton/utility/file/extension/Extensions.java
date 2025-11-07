package com.autobuild.pipeline.definiton.utility.file.extension;

/**
 * Used to fetch extensions for specific script types.
 * 
 * @author Suvabrata Chowdhury
 */

 //TODO: Switch cases are not extensible. Use some other pattern
public class Extensions {
    private static final String SHELL_SCRIPT_EXTENSION = ".sh";

    public static String nameFor(String scriptType) {
        String extension = "";

        switch (scriptType.toLowerCase()) {
            case "bash":
                extension = SHELL_SCRIPT_EXTENSION;
                break;
            default:
                throw new IllegalArgumentException("Given Script type " + scriptType + " is not allowed");
        }

        return extension;
    }
}
