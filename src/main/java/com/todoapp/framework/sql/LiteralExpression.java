package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

/**
 * Represents a literal value in a SQL-like expression.
 * Supports string, boolean, numeric, and date literals.
 */
public class LiteralExpression implements Expression {

    private final Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Object evaluate(TodoItem item) {
        // Literals evaluate to themselves regardless of the item
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}