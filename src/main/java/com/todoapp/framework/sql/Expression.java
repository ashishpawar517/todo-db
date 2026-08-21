package com.todoapp.framework.sql;

/**
 * Base interface for all SQL-like expressions.
 * Represents a value, field reference, or condition that can be evaluated.
 */
public interface Expression {

    /**
     * Evaluates this expression against a TodoItem to produce a value.
     * For conditions, this should return a Boolean.
     * For values, this should return the actual value (String, Boolean, Instant, etc.)
     *
     * @param item the TodoItem to evaluate against
     * @return the evaluated value
     */
    Object evaluate(TodoItem item);
}