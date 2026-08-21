package com.todoapp.framework.sql;

import java.util.Map;

/**
 * Represents an INSERT statement.
 * Currently supports: INSERT INTO table_name (column1, column2, ...) VALUES (value1, value2, ...)
 */
public class InsertStatement implements Statement {

    private final String tableName;
    private final java.util.List<String> columnNames;
    private final java.util.List<Object> values;

    public InsertStatement(String tableName, java.util.List<String> columnNames, java.util.List<Object> values) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.values = values;
    }

    public String getTableName() {
        return tableName;
    }

    public java.util.List<String> getColumnNames() {
        return columnNames;
    }

    public java.util.List<Object> getValues() {
        return values;
    }

    @Override
    public StatementType getType() {
        return StatementType.INSERT;
    }
}