package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

/**
 * Represents a reference to a table column (field) in a SQL-like expression.
 * Maps field names to TodoItem properties.
 */
public class FieldReferenceExpression implements Expression {

    private final String fieldName;

    public FieldReferenceExpression(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Object evaluate(TodoItem item) {
        switch (fieldName.toUpperCase()) {
            case "ID":
                return item.getId();
            case "DESCRIPTION":
                return item.getDescription();
            case "COMPLETED":
                return item.isCompleted();
            case "CREATEDAT":
                return item.getCreatedAt();
            case "COMPLETEDAT":
                return item.getCompletedAt();
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }

    @Override
    public String toString() {
        return fieldName;
    }
}