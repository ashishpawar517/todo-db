package com.todoapp.framework.sql;

/**
 * Interface for parsing SQL-like queries into statement objects.
 * Separates parsing concerns from execution concerns.
 */
public interface SQLParser {

    /**
     * Parses a SQL-like query string into a Statement object.
     *
     * @param sql the SQL-like query string to parse
     * @return a Statement object representing the parsed query
     * @throws SQLParsingException if the SQL string cannot be parsed
     */
    Statement parse(String sql) throws SQLParsingException;
}