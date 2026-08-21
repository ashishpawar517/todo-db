package com.todoapp.framework.sql;

/**
 * Represents a DELETE statement.
 * Currently supports: DELETE FROM table_name [WHERE condition]
 */
public class DeleteStatement implements Statement {

    private final String tableName;
    private final Expression whereClause;

    public DeleteStatement(String tableName, Expression whereClause) {
        this.tableName = tableName;
        this.whereClause = whereClause;
    }

    public String getTableName() {
        return tableName;
    }

    public Expression getWhereClause() {
        return whereClause;
    }

    @Override
    public StatementType getType() {
        return StatementType.DELETE;
    }
}