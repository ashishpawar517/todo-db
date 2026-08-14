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
 * Unit tests for QueryableStorage class.
 * Tests the file-based storage with query capabilities.
 */
class QueryableStorageTest {

    private static final String TEST_FILE_NAME = "test-queryable-todo.txt";
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
    void testSaveAndLoadEmptyList() {
        queryableStorage.save(java.util.List.of());
        List<TodoItem> loaded = queryableStorage.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testSaveAndLoadSingleItem() {
        TodoItem original = new TodoItem("test-id", "Test description");
        queryableStorage.save(java.util.List.of(original));

        List<TodoItem> loaded = queryableStorage.load();
        assertEquals(1, loaded.size());

        TodoItem loadedItem = loaded.get(0);
        assertEquals(original.getId(), loadedItem.getId());
        assertEquals(original.getDescription(), loadedItem.getDescription());
        assertEquals(original.isCompleted(), loadedItem.isCompleted());
        assertEquals(original.getCreatedAt(), loadedItem.getCreatedAt());
        assertEquals(original.getCompletedAt(), loadedItem.getCompletedAt());
    }

    @Test
    void testSaveAndLoadMultipleItems() {
        TodoItem item1 = new TodoItem("id-1", "First item");
        TodoItem item2 = new TodoItem("id-2", "Second item");
        // Complete the second item
        item2.markAsCompleted();

        queryableStorage.save(java.util.List.of(item1, item2));

        List<TodoItem> loaded = queryableStorage.load();
        assertEquals(2, loaded.size());

        // Check first item
        TodoItem loadedItem1 = loaded.get(0);
        assertEquals(item1.getId(), loadedItem1.getId());
        assertEquals(item1.getDescription(), loadedItem1.getDescription());
        assertEquals(item1.isCompleted(), loadedItem1.isCompleted());
        assertEquals(item1.getCreatedAt(), loadedItem1.getCreatedAt());
        assertEquals(item1.getCompletedAt(), loadedItem1.getCompletedAt());

        // Check second item
        TodoItem loadedItem2 = loaded.get(1);
        assertEquals(item2.getId(), loadedItem2.getId());
        assertEquals(item2.getDescription(), loadedItem2.getDescription());
        assertEquals(item2.isCompleted(), loadedItem2.isCompleted());
        assertEquals(item2.getCreatedAt(), loadedItem2.getCreatedAt());
        assertEquals(item2.getCompletedAt(), loadedItem2.getCompletedAt());
    }

    @Test
    void testLoadWhenFileDoesNotExist() {
        List<TodoItem> loaded = queryableStorage.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testLoadWithMalformedData() throws Exception {
        // Create a file with malformed data
        java.nio.file.Files.writeString(testFile.toPath(), "malformed|data\n");

        // Loading should not throw an exception and should return empty list
        List<TodoItem> loaded = queryableStorage.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void testLoadWithEmptyLines() throws Exception {
        // Create a file with empty lines and valid data
        TodoItem item = new TodoItem("test-id", "Test description");
        java.nio.file.Files.writeString(testFile.toPath(),
            "\n\n" +
            formatTodoItemForStorage(item) +
            "\n\n");

        List<TodoItem> loaded = queryableStorage.load();
        assertEquals(1, loaded.size());
        assertEquals(item.getId(), loaded.get(0).getId());
    }

    @Test
    void testLoadWithInvalidLineFormat() throws Exception {
        // Create a file with invalid pipe-separated format
        java.nio.file.Files.writeString(testFile.toPath(),
            "only|two|parts\n" +
            "valid-id|Valid description|false|2023-01-01T10:00:00Z|\n");

        List<TodoItem> loaded = queryableStorage.load();
        assertEquals(1, loaded.size()); // Only the valid line should be loaded
        assertEquals("valid-id", loaded.get(0).getId());
    }

    @Test
    void testStorageWithNullList() {
        assertThrows(NullPointerException.class, () -> {
            queryableStorage.save(null);
        });
    }

    /**
     * Helper method to format a TodoItem as it would appear in storage.
     * This mimics the format used in QueryableStorage.formatTodoItem().
     */
    private String formatTodoItemForStorage(TodoItem item) {
        java.time.format.DateTimeFormatter formatter =
            java.time.format.DateTimeFormatter.ISO_INSTANT;

        String completedAt = (item.getCompletedAt() != null)
            ? formatter.format(item.getCompletedAt())
            : "";

        return String.join("|",
            item.getId(),
            item.getDescription(),
            Boolean.toString(item.isCompleted()),
            formatter.format(item.getCreatedAt()),
            completedAt);
    }
}