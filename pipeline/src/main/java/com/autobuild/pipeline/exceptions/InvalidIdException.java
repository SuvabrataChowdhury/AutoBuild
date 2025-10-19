package com.autobuild.pipeline.exceptions;

/**
 * Exception to throw when id in invalid format are provided.
 * @author Suvabrata Chowdhury
 */

public class InvalidIdException extends Exception {
    public InvalidIdException(String msg) {
        super(msg);
    }
}
