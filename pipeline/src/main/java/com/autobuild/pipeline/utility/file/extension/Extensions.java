package com.autobuild.pipeline.utility.file.extension;

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
