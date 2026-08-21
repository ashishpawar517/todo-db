package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of QueryExecutor that can execute SELECT statements.
 * Other statement types (INSERT, UPDATE, DELETE) will be implemented in future versions.
 */
public class BasicQueryExecutor implements QueryExecutor {

    @Override
    public List<TodoItem> executeSelect(SelectStatement statement, List<TodoItem> items) {
        if (statement == null) {
            throw new IllegalArgumentException("Statement cannot be null");
        }
        if (items == null) {
            throw new IllegalArgumentException("Items list cannot be null");
        }

        List<TodoItem> result = new ArrayList<>();

        // If no WHERE clause, return all items
        if (statement.getWhereClause() == null) {
            result.addAll(items);
            return result;
        }

        // Filter items based on the WHERE clause
        Expression whereClause = statement.getWhereClause();
        for (TodoItem item : items) {
            Object evalResult = whereClause.evaluate(item);
            if (evalResult instanceof Boolean && (Boolean) evalResult) {
                result.add(item);
            }
            // Note: In SQL, WHERE clause should evaluate to boolean.
            // If it doesn't, we treat it as false (don't include the item)
        }

        return result;
    }

    @Override
    public int executeInsert(InsertStatement statement, List<TodoItem> items) {
        // TODO: Implement INSERT statement execution
        // For now, throw UnsupportedOperationException to indicate not yet implemented
        throw new UnsupportedOperationException("INSERT statement execution not yet implemented");
    }

    @Override
    public int executeUpdate(UpdateStatement statement, List<TodoItem> items) {
        // TODO: Implement UPDATE statement execution
        // For now, throw UnsupportedOperationException to indicate not yet implemented
        throw new UnsupportedOperationException("UPDATE statement execution not yet implemented");
    }

    @Override
    public int executeDelete(DeleteStatement statement, List<TodoItem> items) {
        // TODO: Implement DELETE statement execution
        // For now, throw UnsupportedOperationException to indicate not yet implemented
        throw new UnsupportedOperationException("DELETE statement execution not yet implemented");
    }
}