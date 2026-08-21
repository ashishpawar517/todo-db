package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasicQueryExecutorEnhancementsTest {

    private QueryExecutor executor;
    private List<TodoItem> items;

    @BeforeEach
    void setUp() {
        executor = new BasicQueryExecutor();
        items = new ArrayList<>();
        // Add some initial items
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted();
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        items.add(item1);
        items.add(item2);
    }

    @Test
    void testExecuteSelect() throws Exception {
        // Existing functionality
        SelectStatement selectStatement = new SelectStatement("todos",
                new ComparisonExpression(
                        new FieldReferenceExpression("completed"),
                        ComparisonOperator.EQUALS,
                        new LiteralExpression(true)));
        List<TodoItem> result = executor.executeSelect(selectStatement, items);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
    }

    @Test
    void testExecuteInsertDuplicateId() {
        // TODO: Depending on implementation, inserting duplicate ID might throw an exception or update
        // For now, we expect UnsupportedOperationException because INSERT is not implemented
        List<String> columnNames = Arrays.asList("id", "description", "completed", "createdAt", "completedAt");
        List<Object> values = Arrays.asList("1", "Duplicate ID", false, Instant.now(), null);
        InsertStatement insertStatement = new InsertStatement("todos", columnNames, values);
        assertThrows(UnsupportedOperationException.class, () ->
                executor.executeInsert(insertStatement, items));
    }
}