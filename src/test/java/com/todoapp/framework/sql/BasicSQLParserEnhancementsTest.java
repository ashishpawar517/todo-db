package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BasicSQLParserEnhancementsTest {

    private SQLParser parser;

    @BeforeEach
    void setUp() {
        parser = new BasicSQLParser();
    }

    @Test
    void testParseSelectStatement() throws Exception {
        // Existing functionality should still work
        String sql = "SELECT * FROM todos WHERE completed = true AND id = '1'";
        Statement statement = parser.parse(sql);
        assertNotNull(statement);
        assertEquals(StatementType.SELECT, statement.getType());
        SelectStatement selectStatement = (SelectStatement) statement;
        assertEquals("todos", selectStatement.getTableName());
        assertNotNull(selectStatement.getWhereClause());
    }

    @Test
    void testParseInsertStatement() throws Exception {
        // TODO: Once parser supports INSERT, this test should pass
        String sql = "INSERT INTO todos (id, description, completed, createdAt, completedAt) VALUES ('5', 'New task', false, '2026-08-21T10:00:00Z', null)";
        Statement statement = parser.parse(sql);
        assertNotNull(statement);
        assertEquals(StatementType.INSERT, statement.getType());
        InsertStatement insertStatement = (InsertStatement) statement;
        assertEquals("todos", insertStatement.getTableName());
        assertEquals(List.of("id", "description", "completed", "createdAt", "completedAt"), insertStatement.getColumnNames());
        assertEquals(List.of("5", "New task", false, Instant.parse("2026-08-21T10:00:00Z"), null), insertStatement.getValues());
    }

    @Test
    void testParseUpdateStatement() throws Exception {
        // TODO: Once parser supports UPDATE, this test should pass
        String sql = "UPDATE todos SET completed = true WHERE id = '5'";
        Statement statement = parser.parse(sql);
        assertNotNull(statement);
        assertEquals(StatementType.UPDATE, statement.getType());
        UpdateStatement updateStatement = (UpdateStatement) statement;
        assertEquals("todos", updateStatement.getTableName());
        assertEquals(Map.of("completed", true), updateStatement.getAssignments());
        assertNotNull(updateStatement.getWhereClause());
    }

    @Test
    void testParseDeleteStatement() throws Exception {
        // TODO: Once parser supports DELETE, this test should pass
        String sql = "DELETE FROM todos WHERE id = '5'";
        Statement statement = parser.parse(sql);
        assertNotNull(statement);
        assertEquals(StatementType.DELETE, statement.getType());
        DeleteStatement deleteStatement = (DeleteStatement) statement;
        assertEquals("todos", deleteStatement.getTableName());
        assertNotNull(deleteStatement.getWhereClause());
    }

    @Test
    void testParseUnsupportedStatementThrowsException() {
        // Currently, the parser throws an exception for non-SELECT statements
        String sql = "INSERT INTO todos VALUES ('1', 'test')";
        assertThrows(SQLParsingException.class, () -> parser.parse(sql));
    }
}