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

class QueryableStorageTransactionTest {

    private static final String TEST_FILE_NAME = "test-transaction-todo.txt";
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
    void testTransactionMethodsThrowExceptionWhenNotImplemented() {
        // Currently, these methods are not implemented, so we expect an exception
        assertThrows(UnsupportedOperationException.class, () -> queryableStorage.beginTransaction());
        assertThrows(UnsupportedOperationException.class, () -> queryableStorage.commit());
        assertThrows(UnsupportedOperationException.class, () -> queryableStorage.rollback());
    }
}