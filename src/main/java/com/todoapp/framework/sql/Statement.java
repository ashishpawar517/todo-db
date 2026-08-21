package com.todoapp.framework.sql;

/**
 * Base interface for all SQL-like statements.
 * Represents a parsed SQL query that can be executed.
 */
public interface Statement {

    /**
     * Gets the type of this statement.
     * @return the statement type
     */
    StatementType getType();
}