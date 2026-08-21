package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

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
        System.out.println("SQL: " + sql);
        System.out.println("Upper SQL: " + sql.toUpperCase());
        Statement statement = parser.parse(sql);
        System.out.println("Statement: " + statement);
        assertNotNull(statement);
        assertEquals(StatementType.SELECT, statement.getType());
        SelectStatement selectStatement = (SelectStatement) statement;
        assertEquals("todos", selectStatement.getTableName());
        assertNotNull(selectStatement.getWhereClause());
    }

    @Test
    void testParseUnsupportedStatementThrowsException() {
        // Currently, the parser throws an exception for non-SELECT statements
        String sql = "INSERT INTO todos VALUES ('1', 'test')";
        assertThrows(SQLParsingException.class, () -> parser.parse(sql));
    }
}