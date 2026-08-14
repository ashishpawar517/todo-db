package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.StorageGateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Debug test for query issues.
 */
class QueryDebugTest {

    private static final String TEST_FILE_NAME = "debug-test-todo.txt";
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
    void testDebugAndOrQuery() {
        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted();
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        TodoItem item3 = new TodoItem("3", "Write report");
        item3.markAsCompleted();
        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);
        queryableStorage.addItem(item3);

        // Debug output
        System.out.println("Stored items:");
        List<TodoItem> allItems = queryableStorage.load();
        for (TodoItem item : allItems) {
            System.out.println("  " + item.getId() + ": " + item.getDescription() +
                              ", completed=" + item.isCompleted());
        }

        // Test the problematic query
        System.out.println("\nTesting: SELECT * FROM todos WHERE completed = true AND (id = '1' OR id = '3')");
        List<TodoItem> result = queryableStorage.executeQuery("SELECT * FROM todos WHERE completed = true AND (id = '1' OR id = '3')");
        System.out.println("Result count: " + result.size());
        for (TodoItem item : result) {
            System.out.println("  " + item.getId() + ": " + item.getDescription());
        }

        // Verify correct results
        assertEquals(2, result.size(), "Should return 2 items");
        assertTrue(result.stream().anyMatch(item -> item.getId().equals("1")), "Should contain item 1");
        assertTrue(result.stream().anyMatch(item -> item.getId().equals("3")), "Should contain item 3");
        assertTrue(result.stream().allMatch(TodoItem::isCompleted), "All results should be completed");
    }
}