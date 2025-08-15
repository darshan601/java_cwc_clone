package com.codingchallenge.ccwc.exception;

/**
 * Exception thrown when invalid command line arguments are provided.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class InvalidArgumentException extends CcwcException {

    public InvalidArgumentException(String message) {
        super(message);
    }

    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
