package com.todoapp.framework.sql;

import com.todoapp.domain.TodoItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    void testExecuteInsert() throws Exception {
        // TODO: Once executor supports INSERT, this test should pass
        InsertStatement insertStatement = new InsertStatement("todos",
                List.of("id", "description", "completed", "createdAt", "completedAt"),
                List.of("3", "Write report", false, Instant.now(), null));
        int rowsInserted = executor.executeInsert(insertStatement, items);
        assertEquals(1, rowsInserted);
        assertEquals(3, items.size());
        TodoItem inserted = items.get(2);
        assertEquals("3", inserted.getId());
        assertEquals("Write report", inserted.getDescription());
        assertFalse(inserted.isCompleted());
    }

    @Test
    void testExecuteUpdate() throws Exception {
        // TODO: Once executor supports UPDATE, this test should pass
        UpdateStatement updateStatement = new UpdateStatement("todos",
                Map.of("completed", true),
                new ComparisonExpression(
                        new FieldReferenceExpression("id"),
                        ComparisonOperator.EQUALS,
                        new LiteralExpression("2")));
        int rowsUpdated = executor.executeUpdate(updateStatement, items);
        assertEquals(1, rowsUpdated);
        assertTrue(items.get(1).isCompleted()); // item with id "2" should now be completed
    }

    @Test
    void testExecuteDelete() throws Exception {
        // TODO: Once executor supports DELETE, this test should pass
        DeleteStatement deleteStatement = new DeleteStatement("todos",
                new ComparisonExpression(
                        new FieldReferenceExpression("id"),
                        ComparisonOperator.EQUALS,
                        new LiteralExpression("1")));
        int rowsDeleted = executor.executeDelete(deleteStatement, items);
        assertEquals(1, rowsDeleted);
        assertEquals(1, items.size());
        assertEquals("2", items.get(0).getId()); // remaining item should be id "2"
    }

    @Test
    void testExecuteInsertDuplicateId() {
        // TODO: Depending on implementation, inserting duplicate ID might throw an exception or update
        // For now, we expect UnsupportedOperationException because INSERT is not implemented
        InsertStatement insertStatement = new InsertStatement("todos",
                List.of("id", "description", "completed", "createdAt", "completedAt"),
                List.of("1", "Duplicate ID", false, Instant.now(), null));
        assertThrows(UnsupportedOperationException.class, () ->
                executor.executeInsert(insertStatement, items));
    }
}