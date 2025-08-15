package com.codingchallenge.ccwc.exception;

/**
 * Base exception class for all ccwc related exceptions.
 * 
 * @author Coding Challenge
 * @version 1.0.0
 */
public class CcwcException extends Exception {

    public CcwcException(String message) {
        super(message);
    }

    public CcwcException(String message, Throwable cause) {
        super(message, cause);
    }
}
