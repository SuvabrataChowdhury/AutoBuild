package com.autobuild.pipeline.exceptions;

/**
 * Exception to throw when duplicate entry is provided.
 * @author Suvabrata Chowdhury
 */
public class DuplicateEntryException extends Exception{
    public DuplicateEntryException(String msg) {
        super(msg);
    }
}
