package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.StorageGateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for QueryableStorage with QueryEngine.
 * Tests the executeQuery functionality end-to-end.
 */
class QueryableStorageIntegrationTest {

    private static final String TEST_FILE_NAME = "test-queryable-integration-todo.txt";
    private QueryableStorage queryableStorage;
    private File testFile;

    @BeforeEach
    void setUp() {
        queryableStorage = new QueryableStorage(TEST_FILE_NAME);
        testFile = new File(TEST_FILE_NAME);
        // Delete test file if it exists from previous runs
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up test file after each test
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testExecuteQueryReturnsAllItemsWhenNoWhereClause() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Execute query with no WHERE clause
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos");
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(item1, item2)));
    }

    @Test
    void testExecuteQueryWithEqualsConditionOnId() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Execute query with ID condition
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE id = '2'");
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getId());
    }

    @Test
    void testExecuteQueryWithEqualsConditionOnCompletedTrue() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted();
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        // Leave item2 as incomplete
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Execute query for completed items
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE completed = true");
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void testExecuteQueryWithEqualsConditionOnCompletedFalse() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted();
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        // Leave item2 as incomplete
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Execute query for incomplete items
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE completed = false");
        assertEquals(1, result.size());
        assertFalse(result.get(0).isCompleted());
        assertEquals("2", result.get(0).getId());
    }

    @Test
    void testExecuteQueryWithAndOperator() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted();
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        // Leave item2 as incomplete
        TodoItem item3 = new TodoItem("3", "Write report");
        item3.markAsCompleted();
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);
        queryableStorage.addItem(item3);

        // Execute query with AND condition
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE completed = true AND id = '1'");
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertTrue(result.get(0).isCompleted());
    }

    @Test
    void testExecuteQueryWithOrOperator() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        TodoItem item3 = new TodoItem("3", "Write report");
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);
        queryableStorage.addItem(item3);

        // Execute query with OR condition
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE id = '1' OR id = '3'");
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getId().equals("1")));
        assertTrue(result.stream().anyMatch(item -> item.getId().equals("3")));
    }

    @Test
    void testExecuteQueryWithIsNullCheck() {
        // Add test data - all items have IDs set, so IS NULL should return empty
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Execute IS NULL query on ID (should return empty)
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE id IS NULL");
        assertTrue(result.isEmpty());

        // For completedAt, newly created items have null completedAt
        List<TodoItem> result2 = queryableStorage.executeQuery("SELECT * FROM todos WHERE completedAt IS NULL");
        assertEquals(2, result2.size());
        assertTrue(result2.stream().allMatch(item -> item.getCompletedAt() == null));
    }

    @Test
    void testExecuteQueryWithIsNotNullCheck() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted(); // This sets completedAt
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        // Leave item2 as incomplete (completedAt remains null)
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Execute IS NOT NULL query on completedAt
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE completedAt IS NOT NULL");
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getCompletedAt());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void testExecuteQueryWithStringFieldDescription() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Execute query on description field
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE description = 'Walk the dog'");
        assertEquals(1, result.size());
        assertEquals("Walk the dog", result.get(0).getDescription());
    }

    @Test
    void testExecuteQueryWorksAfterAddUpdateRemove() {
        // Add initial data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Verify initial state
        List<TodoItem> initialResult = queryableStorage.executeQuery("SELECT * FROM todos");
        assertEquals(2, initialResult.size());

        // Add an item
        TodoItem item3 = new TodoItem("3", "Write report");
        queryableStorage.addItem(item3);
        List<TodoItem> afterAdd = queryableStorage.executeQuery("SELECT * FROM todos");
        assertEquals(3, afterAdd.size());

        // Update an item
        item1.markAsCompleted();
        queryableStorage.updateItem(item1);
        List<TodoItem> completedResult = queryableStorage.executeQuery("SELECT * FROM todos WHERE completed = true");
        assertEquals(1, completedResult.size());
        assertTrue(completedResult.get(0).isCompleted());

        // Remove an item
        queryableStorage.removeItem("2");
        List<TodoItem> afterRemove = queryableStorage.executeQuery("SELECT * FROM todos");
        assertEquals(2, afterRemove.size());
        assertFalse(afterRemove.stream().anyMatch(item -> item.getId().equals("2")));
    }

    @Test
    void testExecuteQueryReturnsEmptyListForNoMatches() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        // Execute query that should return no results
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE id = '999'");
        assertTrue(result.isEmpty());
    }

    @Test
    void testExecuteQueryHandlesEmptyStorageGracefully() {
        // Execute query on empty storage
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE completed = true");
        assertTrue(result.isEmpty());
    }
}