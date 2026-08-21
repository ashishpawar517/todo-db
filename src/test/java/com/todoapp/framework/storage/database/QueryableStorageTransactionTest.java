package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.StorageGateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

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
    void testTransactionCommit() throws Exception {
        // TODO: Once transactions are supported, this test should pass
        queryableStorage.beginTransaction();

        // Add an item within the transaction
        TodoItem item = new TodoItem("1", "Buy groceries");
        queryableStorage.addItem(item);

        // Update an item (none yet, so we add another first)
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        queryableStorage.addItem(item2);
        item2.markAsCompleted();
        queryableStorage.updateItem(item2);

        // Commit the transaction
        queryableStorage.commit();

        // Verify changes are persisted
        List<TodoItem> items = queryableStorage.load();
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getId().equals("1") && !i.isCompleted()));
        assertTrue(items.stream().anyMatch(i -> i.getId().equals("2") && i.isCompleted()));
    }

    @Test
    void testTransactionRollback() throws Exception {
        // TODO: Once transactions are supported, this test should pass
        queryableStorage.beginTransaction();

        // Add an item within the transaction
        TodoItem item = new TodoItem("1", "Buy groceries");
        queryableStorage.addItem(item);

        // Rollback the transaction
        queryableStorage.rollback();

        // Verify the item was not persisted
        List<TodoItem> items = queryableStorage.load();
        assertTrue(items.isEmpty(), "No items should be present after rollback");
    }

    @Test
    void testTransactionRollbackOnException() throws Exception {
        // TODO: Once transactions are supported, this test should pass
        try {
            queryableStorage.beginTransaction();

            // Add an item
            TodoItem item = new TodoItem("1", "Buy groceries");
            queryableStorage.addItem(item);

            // Simulate an error
            throw new RuntimeException("Simulated error");
        } catch (RuntimeException e) {
            // Expected
        }
        // Assuming the implementation rolls back on exception or requires explicit rollback
        // We'll test that after an exception, if we rollback, the item is not there
        // But first, we need to check if the transaction is still active.
        // For simplicity, we'll assume the user must rollback.
        queryableStorage.rollback();

        List<TodoItem> items = queryableStorage.load();
        assertTrue(items.isEmpty(), "Items should be rolled back after exception and rollback");
    }

    @Test
    void testTransactionIsolation() throws Exception {
        // TODO: Once transactions are supported, this test should pass
        // Start a transaction and add an item
        queryableStorage.beginTransaction();
        TodoItem item = new TodoItem("1", "Buy groceries");
        queryableStorage.addItem(item);

        // Without committing, the item should not be visible in a new storage instance
        // (if the storage is file-based and transactions are implemented with a temporary copy)
        QueryableStorage otherStorage = new QueryableStorage(TEST_FILE_NAME);
        List<TodoItem> itemsInOther = otherStorage.load();
        assertTrue(itemsInOther.isEmpty(), "Uncommitted changes should not be visible to other storage instances");

        // Commit and verify now it is visible
        queryableStorage.commit();
        List<TodoItem> itemsAfterCommit = otherStorage.load();
        assertEquals(1, itemsAfterCommit.size());
        assertEquals("1", itemsAfterCommit.get(0).getId());

        otherStorage = null; // close resource
    }

    @Test
    void testTransactionalSqlOperations() throws Exception {
        // TODO: Once transactions and SQL operations are supported, this test should pass
        queryableStorage.beginTransaction();

        String insertSql = "INSERT INTO todos (id, description, completed, createdAt, completedAt) VALUES ('1', 'Buy groceries', false, '2026-08-21T10:00:00Z', null)";
        queryableStorage.executeInsert(insertSql);

        String updateSql = "UPDATE todos SET completed = true WHERE id = '1'";
        queryableStorage.executeUpdate(updateSql);

        queryableStorage.commit();

        List<TodoItem> items = queryableStorage.load();
        assertEquals(1, items.size());
        TodoItem item = items.get(0);
        assertEquals("1", item.getId());
        assertTrue(item.isCompleted());
    }

    @Test
    void testTransactionMethodsThrowExceptionWhenNotImplemented() {
        // Currently, these methods are not implemented, so we expect an exception
        assertThrows(UnsupportedOperationException.class, () -> queryableStorage.beginTransaction());
        assertThrows(UnsupportedOperationException.class, () -> queryableStorage.commit());
        assertThrows(UnsupportedOperationException.class, () -> queryableStorage.rollback());
    }
}