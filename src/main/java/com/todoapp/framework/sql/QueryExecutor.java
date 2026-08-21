package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

import java.util.List;

/**
 * Interface for executing SQL-like statements against a collection of TodoItem objects.
 * Separates execution concerns from parsing concerns.
 */
public interface QueryExecutor {

    /**
     * Executes a SELECT statement against the given items and returns the matching items.
     *
     * @param statement the SELECT statement to execute
     * @param items the collection of TodoItem objects to query against
     * @return a list of matching TodoItem objects
     */
    List<TodoItem> executeSelect(SelectStatement statement, List<TodoItem> items);

    /**
     * Executes an INSERT statement against the given items.
     * Modifies the items list in-place to add the new item(s).
     *
     * @param statement the INSERT statement to execute
     * @param items the collection of TodoItem objects to insert into
     * @return the number of rows inserted
     */
    int executeInsert(InsertStatement statement, List<TodoItem> items);

    /**
     * Executes an UPDATE statement against the given items.
     * Modifies the items list in-place to update matching items.
     *
     * @param statement the UPDATE statement to execute
     * @param items the collection of TodoItem objects to update
     * @return the number of rows updated
     */
    int executeUpdate(UpdateStatement statement, List<TodoItem> items);

    /**
     * Executes a DELETE statement against the given items.
     * Modifies the items list in-place to remove matching items.
     *
     * @param statement the DELETE statement to execute
     * @param items the collection of TodoItem objects to delete from
     * @return the number of rows deleted
     */
    int executeDelete(DeleteStatement statement, List<TodoItem> items);
}