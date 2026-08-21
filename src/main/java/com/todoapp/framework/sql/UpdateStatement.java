package com.todoapp.framework.sql;

import java.util.Map;

/**
 * Represents an UPDATE statement.
 * Currently supports: UPDATE table_name SET column1 = value1, column2 = value2, ... [WHERE condition]
 */
public class UpdateStatement implements Statement {

    private final String tableName;
    private final java.util.Map<String, Object> assignments;
    private final Expression whereClause;

    public UpdateStatement(String tableName, java.util.Map<String, Object> assignments, Expression whereClause) {
        this.tableName = tableName;
        this.assignments = assignments;
        this.whereClause = whereClause;
    }

    public String getTableName() {
        return tableName;
    }

    public java.util.Map<String, Object> getAssignments() {
        return assignments;
    }

    public Expression getWhereClause() {
        return whereClause;
    }

    @Override
    public StatementType getType() {
        return StatementType.UPDATE;
    }
}