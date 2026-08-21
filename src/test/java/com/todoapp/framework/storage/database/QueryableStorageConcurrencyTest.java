package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class QueryableStorageConcurrencyTest {

    private static final String TEST_FILE_NAME = "test-concurrency-todo.txt";
    private QueryableStorage queryableStorage;
    private File testFile;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() throws Exception {
        queryableStorage = new QueryableStorage(TEST_FILE_NAME);
        testFile = new File(TEST_FILE_NAME);
        // Delete test file if it exists from previous runs
        if (testFile.exists()) {
            testFile.delete();
        }
        executorService = Executors.newFixedThreadPool(10);
    }

    @AfterEach
    void tearDown() throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        // Clean up test file after each test
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testConcurrentAddOperations() throws Exception {
        int numberOfThreads = 10;
        int itemsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < itemsPerThread; j++) {
                        String id = Thread.currentThread().getId() + "-" + threadId + "-" + j;
                        TodoItem item = new TodoItem(id, "Task " + j);
                        queryableStorage.addItem(item);
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        assertEquals(0, errorCount.get(), "No errors should occur during concurrent adds");

        // Verify all items were added
        List<TodoItem> items = queryableStorage.load();
        int expectedTotal = numberOfThreads * itemsPerThread;
        assertEquals(expectedTotal, items.size(), "All items should be present");

        // Additionally, verify that each item can be retrieved by ID
        for (TodoItem item : items) {
            TodoItem found = queryableStorage.load().stream()
                    .filter(i -> i.getId().equals(item.getId()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(found, "Item with ID " + item.getId() + " should be found");
        }
    }

    @Test
    void testConcurrentMixedOperations() throws Exception {
        int numberOfThreads = 5;
        int operationsPerThread = 50;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger errorCount = new AtomicInteger(0);

        // Start with some initial data
        for (int i = 0; i < 20; i++) {
            TodoItem item = new TodoItem("initial-" + i, "Initial task " + i);
            queryableStorage.addItem(item);
        }

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int choice = java.util.concurrent.ThreadLocalRandom.current().nextInt(4);
                        switch (choice) {
                            case 0: // Add
                                String id = Thread.currentThread().getId() + "-add-" + j;
                                TodoItem item = new TodoItem(id, "Added task " + j);
                                queryableStorage.addItem(item);
                                break;
                            case 1: // Update
                                List<TodoItem> items = queryableStorage.load();
                                if (!items.isEmpty()) {
                                    TodoItem itemToUpdate = items.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(items.size()));
                                    itemToUpdate.markAsCompleted();
                                    queryableStorage.updateItem(itemToUpdate);
                                }
                                break;
                            case 2: // Remove
                                List<TodoItem> items2 = queryableStorage.load();
                                if (!items2.isEmpty()) {
                                    TodoItem itemToRemove = items2.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(items2.size()));
                                    queryableStorage.removeItem(itemToRemove.getId());
                                }
                                break;
                            case 3: // Query
                                queryableStorage.executeQuery("SELECT * FROM todos");
                                break;
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        assertEquals(0, errorCount.get(), "No errors should occur during concurrent mixed operations");

        // Final verification: storage should be in a consistent state
        List<TodoItem> finalItems = queryableStorage.load();
        // Just ensure we can load the list without exception
        assertNotNull(finalItems);
    }
}