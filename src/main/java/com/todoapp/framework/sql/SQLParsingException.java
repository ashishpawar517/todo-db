package com.todoapp.framework.sql;

/**
 * Exception thrown when a SQL-like query string cannot be parsed.
 */
public class SQLParsingException extends Exception {

    public SQLParsingException(String message) {
        super(message);
    }

    public SQLParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}