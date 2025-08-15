package com.codingchallenge.ccwc.exception;

/**
 * Exception thrown when file operations fail.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class FileOperationException extends CcwcException {

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
