package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade that combines SQLParser and QueryExecutor to provide a simple interface
 * for executing SQL-like queries, similar to the old QueryEngine but with better
 * separation of concerns.
 */
public class SQLEngine {

    private final SQLParser parser;
    private final QueryExecutor executor;

    public SQLEngine(SQLParser parser, QueryExecutor executor) {
        this.parser = parser;
        this.executor = executor;
    }

    /**
     * Executes a SQL-like query string against the given items and returns the results.
     * This method mirrors the interface of the old QueryEngine.executeQuery method
     * for backward compatibility.
     *
     * @param sql   the SQL-like query string to execute
     * @param items the collection of TodoItem objects to query against
     * @return a list of matching TodoItem objects (for SELECT queries)
     *         For non-SELECT queries, returns an empty list (maintaining old behavior)
     */
    public List<TodoItem> executeQuery(String sql, List<TodoItem> items) {
        try {
            Statement statement = parser.parse(sql);

            // For now, we only handle SELECT queries in executeQuery
            // INSERT, UPDATE, DELETE would modify the data and return count
            if (statement.getType() == StatementType.SELECT) {
                SelectStatement selectStmt = (SelectStatement) statement;
                return executor.executeSelect(selectStmt, items);
            } else {
                // For non-SELECT queries, we don't return items in this method
                // In a full implementation, we might have separate execute methods
                // that return row counts, similar to JDBC
                return new ArrayList<>();
            }
        } catch (SQLParsingException e) {
            // In the old implementation, malformed queries returned all items
            // We'll maintain that behavior for backward compatibility
            return new ArrayList<>(items);
        } catch (Exception e) {
            // Other execution errors also return all items for backward compatibility
            return new ArrayList<>(items);
        }
    }

    /**
     * Executes a SQL-like statement string against the given items.
     * For non-SELECT statements, this returns the number of affected rows.
     *
     * @param sql   the SQL-like statement string to execute
     * @param items the collection of TodoItem objects to execute against
     * @return the number of rows affected (for INSERT, UPDATE, DELETE)
     *         For SELECT queries, returns 0 (not meaningful for SELECT)
     */
    public int executeUpdate(String sql, List<TodoItem> items) {
        try {
            Statement statement = parser.parse(sql);

            switch (statement.getType()) {
                case INSERT:
                    InsertStatement insertStmt = (InsertStatement) statement;
                    return executor.executeInsert(insertStmt, items);
                case UPDATE:
                    UpdateStatement updateStmt = (UpdateStatement) statement;
                    return executor.executeUpdate(updateStmt, items);
                case DELETE:
                    DeleteStatement deleteStmt = (DeleteStatement) statement;
                    return executor.executeDelete(deleteStmt, items);
                case SELECT:
                    // For SELECT queries, executeUpdate returns 0 (not meaningful)
                    // Users should use executeQuery for SELECT statements
                    return 0;
                default:
                    return 0;
            }
        } catch (SQLParsingException e) {
            // Malformed statement affects 0 rows
            return 0;
        } catch (Exception e) {
            // Execution errors affect 0 rows
            return 0;
        }
    }
}