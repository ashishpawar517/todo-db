package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.StorageGateway;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Completely isolated test using in-memory lists to eliminate file I/O issues.
 */
class IsolatedQueryTest {

    @Test
    void testIsolatedAndOrLogic() {
        // Create test data directly
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted();
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        TodoItem item3 = new TodoItem("3", "Write report");
        item3.markAsCompleted();

        List<TodoItem> items = Arrays.asList(item1, item2, item3);

        // Create query engine and load items
        QueryEngine queryEngine = new QueryEngine();
        queryEngine.loadItems(items);

        // Test the problematic query directly on the query engine
        System.out.println("Testing isolated query engine:");
        System.out.println("Query: SELECT * FROM todos WHERE completed = true AND (id = '1' OR id = '3')");
        List<TodoItem> result = queryEngine.executeQuery("SELECT * FROM todos WHERE completed = true AND (id = '1' OR id = '3')", items);
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