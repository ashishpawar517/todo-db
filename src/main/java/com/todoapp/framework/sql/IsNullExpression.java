package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

/**
 * Represents an IS NULL or IS NOT NULL expression in a SQL-like WHERE clause.
 */
public class IsNullExpression implements Expression {

    private final Expression expression;
    private final boolean isNull; // true for IS NULL, false for IS NOT NULL

    public IsNullExpression(Expression expression, boolean isNull) {
        this.expression = expression;
        this.isNull = isNull;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean isNull() {
        return isNull;
    }

    @Override
    public Object evaluate(TodoItem item) {
        Object value = expression.evaluate(item);
        boolean isNullValue = (value == null);
        return isNull == isNullValue; // Return true if the nullness matches what we're checking for
    }

    @Override
    public String toString() {
        return expression + " IS " + (isNull ? "NULL" : "NOT NULL");
    }
}