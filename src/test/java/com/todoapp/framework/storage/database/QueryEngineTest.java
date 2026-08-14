package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QueryEngine class.
 * Tests the SQL-like query engine functionality.
 */
class QueryEngineTest {

    private QueryEngine queryEngine;
    private List<TodoItem> testItems;

    @BeforeEach
    void setUp() {
        queryEngine = new QueryEngine();

        // Create test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted();

        TodoItem item2 = new TodoItem("2", "Walk the dog");
        // Leave item2 as incomplete

        TodoItem item3 = new TodoItem("3", "Write report");
        item3.markAsCompleted();

        // Set specific timestamps for testing
        TodoItem item4 = new TodoItem("4", "Old task");
        // Manually create with specific timestamp (8 days ago)
        TodoItem item4WithTime = new TodoItem("4", "Old task", false,
            Instant.now().minus(java.time.Duration.ofDays(8)), null);

        testItems = List.of(item1, item2, item3, item4WithTime);
        queryEngine.loadItems(testItems);
    }

    @Test
    void testExecuteQueryReturnsAllItemsWhenNoWhereClause() {
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos", testItems);
        assertEquals(4, result.size());
        assertTrue(result.containsAll(testItems));
    }

    @Test
    void testExecuteQueryWithEqualsConditionOnId() {
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE id = '2'", testItems);
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getId());
    }

    @Test
    void testExecuteQueryWithNotEqualsConditionOnId() {
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE id != '2'", testItems);
        assertEquals(3, result.size());
        assertFalse(result.stream().anyMatch(item -> item.getId().equals("2")));
    }

    @Test
    void testExecuteQueryWithEqualsConditionOnCompletedTrue() {
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE completed = true", testItems);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(TodoItem::isCompleted));
    }

    @Test
    void testExecuteQueryWithEqualsConditionOnCompletedFalse() {
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE completed = false", testItems);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> !item.isCompleted()));
    }

    @Test
    void testExecuteQueryWithAndOperator() {
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE completed = true AND id = '1'", testItems);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertTrue(result.get(0).isCompleted());
    }

    @Test
    void testExecuteQueryWithOrOperator() {
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE id = '1' OR id = '3'", testItems);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getId().equals("1")));
        assertTrue(result.stream().anyMatch(item -> item.getId().equals("3")));
    }

    @Test
    void testExecuteQueryWithIsNullCheck() {
        // All items have non-null IDs, so IS NULL should return empty
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE id IS NULL", testItems);
        assertTrue(result.isEmpty());

        // completedAt is null for incomplete items
        List<TodoItem> result2 = queryEngine.executeQuery("SELECT * FROM todos WHERE completedAt IS NULL", testItems);
        assertEquals(2, result2.size()); // item2 and item4 have null completedAt
        assertTrue(result2.stream().allMatch(item -> item.getCompletedAt() == null));
    }

    @Test
    void testExecuteQueryWithIsNotNullCheck() {
        // completedAt is not null for completed items
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE completedAt IS NOT NULL", testItems);
        assertEquals(2, result.size()); // item1 and item3 have non-null completedAt
        assertTrue(result.stream().allMatch(item -> item.getCompletedAt() != null));
    }

    @Test
    void testExecuteQueryWithStringFieldDescription() {
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE description = 'Walk the dog'", testItems);
        assertEquals(1, result.size());
        assertEquals("Walk the dog", result.get(0).getDescription());
    }

    @Test
    void testExecuteQueryWithMalformedQueryReturnsAllItems() {
        // Malformed query should return all items (current implementation behavior)
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE", testItems);
        assertEquals(4, result.size());
        assertTrue(result.containsAll(testItems));
    }

    @Test
    void testExecuteQueryWithUnsupportedQueryFormatReturnsAllItems() {
        // Unsupported query format should return all items
        List<TodoItem> result = queryEngine.executeQuery("UPDATE todos SET completed = true WHERE id = '1'", testItems);
        assertEquals(4, result.size());
        assertTrue(result.containsAll(testItems));
    }
}