package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.StorageGateway;
import com.todoapp.framework.storage.database.QueryableStorage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryableStorageSQLOperationsTest {

    private static final String TEST_FILE_NAME = "test-sql-operations-todo.txt";
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
    void testExecuteQueryViaSqlStillWorks() throws Exception {
        // Ensure that the existing SELECT functionality still works
        TodoItem item = new TodoItem("1", "Buy groceries");
        item.markAsCompleted();
        queryableStorage.addItem(item);

        String sql = "SELECT * FROM todos WHERE completed = true";
        List<TodoItem> result = queryableStorage.executeQuery(sql);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
    }

    @Test
    void testExecuteInsertThrowsExceptionWhenNotImplemented() {
        // Currently, these methods are not implemented, so we expect an exception
        String sql = "INSERT INTO todos VALUES ('1', 'test')";
        assertThrows(UnsupportedOperationException.class, () ->
                queryableStorage.executeInsert(sql));
    }

    @Test
    void testExecuteUpdateThrowsExceptionWhenNotImplemented() {
        String sql = "UPDATE todos SET completed = true WHERE id = '1'";
        assertThrows(UnsupportedOperationException.class, () ->
                queryableStorage.executeUpdate(sql));
    }

    @Test
    void testExecuteDeleteThrowsExceptionWhenNotImplemented() {
        String sql = "DELETE FROM todos WHERE id = '1'";
        assertThrows(UnsupportedOperationException.class, () ->
                queryableStorage.executeDelete(sql));
    }
}