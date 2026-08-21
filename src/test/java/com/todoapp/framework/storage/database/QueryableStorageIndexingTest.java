package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.StorageGateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryableStorageIndexingTest {

    private static final String TEST_FILE_NAME = "test-indexing-todo.txt";
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
    void testStorageStillWorksCorrectly() {
        // This test ensures that basic functionality is not broken by indexing changes
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        item2.markAsCompleted();

        queryableStorage.addItem(item1);
        queryableStorage.addItem(item2);

        List<TodoItem> allItems = queryableStorage.load();
        assertEquals(2, allItems.size());

        List<TodoItem> completedItems = queryableStorage.executeQuery("SELECT * FROM todos WHERE completed = true");
        assertEquals(1, completedItems.size());
        assertEquals("2", completedItems.get(0).getId());

        List<TodoItem> incompleteItems = queryableStorage.executeQuery("SELECT * FROM todos WHERE completed = false");
        assertEquals(1, incompleteItems.size());
        assertEquals("1", incompleteItems.get(0).getId());
    }

    // TODO: Add indexing-specific tests once indexing mechanism is implemented
    // For example:
    // testThatIndexesAreCreatedOnLoad()
    // testThatIndexUsageIsTracked()
    // testThatUpdatesMaintainIndexes()
    // testThatQueriesUseIndexesForPerformance()
}