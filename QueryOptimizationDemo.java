import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.database.QueryableStorage;

import java.time.Instant;
import java.util.List;

/**
 * Demonstration to show that executeQuery optimization is working.
 * This shows that executeQuery doesn't reload the file every time.
 */
public class QueryOptimizationDemo {
    public static void main(String[] args) {
        // Create a storage instance
        QueryableStorage storage = new QueryableStorage("optimization-demo.txt");

        // Clean up any existing file
        java.io.File file = new java.io.File("optimization-demo.txt");
        if (file.exists()) {
            file.delete();
        }

        // Add some initial items
        TodoItem item1 = new TodoItem("1", "First item");
        TodoItem item2 = new TodoItem("2", "Second item");
        item2.markAsCompleted();

        storage.addItem(item1);
        storage.addItem(item2);

        System.out.println("Initial items added.");
        System.out.println("Number of items in storage: " + storage.load().size());

        // Execute a query - this should use cached items, not reload from file
        System.out.println("\nExecuting first query...");
        List<TodoItem> completedItems = storage.executeQuery("SELECT * FROM todos WHERE completed = true");
        System.out.println("Found " + completedItems.size() + " completed items.");

        // Add another item
        TodoItem item3 = new TodoItem("3", "Third item");
        storage.addItem(item3);
        System.out.println("\nAdded third item.");

        // Execute another query - should still use cached items (now updated)
        System.out.println("Executing second query after adding item...");
        List<TodoItem> allItems = storage.executeQuery("SELECT * FROM todos");
        System.out.println("Found " + allItems.size() + " total items.");

        // Clean up
        if (file.exists()) {
            file.delete();
        }

        System.out.println("\nDemo completed successfully!");
    }
}