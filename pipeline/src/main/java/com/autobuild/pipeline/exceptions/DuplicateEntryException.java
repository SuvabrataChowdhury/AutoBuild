package com.autobuild.pipeline.exceptions;

public class DuplicateEntryException extends Exception{
    public DuplicateEntryException(String msg) {
        super(msg);
    }

    public DuplicateEntryException() {
        super();
    }
}
