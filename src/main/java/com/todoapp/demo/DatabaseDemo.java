package com.todoapp.demo;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.database.QueryableStorage;

import java.time.Instant;
import java.util.List;

/**
 * Simple demonstration of the QueryableStorage functionality.
 * Shows how to use the SQL-like query capabilities.
 */
public class DatabaseDemo {
    public static void main(String[] args) {
        // Create a queryable storage instance
        QueryableStorage storage = new QueryableStorage("demo-todo.txt");

        System.out.println("=== Todo List Database Demo ===\n");

        // Add some sample todo items
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        TodoItem item3 = new TodoItem("3", "Write report");
        TodoItem item4 = new TodoItem("4", "Call mom");

        // Mark some items as completed
        item1.markAsCompleted();
        item3.markAsCompleted();

        // Add items to storage
        storage.addItem(item1);
        storage.addItem(item2);
        storage.addItem(item3);
        storage.addItem(item4);

        System.out.println("Added 4 todo items (2 completed, 2 incomplete)\n");

        // Demonstrate various queries

        // 1. Get all items
        System.out.println("1. All items:");
        List<TodoItem> allItems = storage.executeQuery("SELECT * FROM todos");
        allItems.forEach(item ->
            System.out.printf("   %s: %s [%s]%n",
                item.getId(),
                item.getDescription(),
                item.isCompleted() ? "��✓" : "��✗"));
        System.out.println();

        // 2. Get only completed items
        System.out.println("2. Completed items:");
        List<TodoItem> completedItems = storage.executeQuery("SELECT * FROM todos WHERE completed = true");
        completedItems.forEach(item ->
            System.out.printf("   %s: %s%n", item.getId(), item.getDescription()));
        System.out.println();

        // 3. Get only incomplete items
        System.out.println("3. Incomplete items:");
        List<TodoItem> incompleteItems = storage.executeQuery("SELECT * FROM todos WHERE completed = false");
        incompleteItems.forEach(item ->
            System.out.printf("   %s: %s%n", item.getId(), item.getDescription()));
        System.out.println();

        // 4. Get items by specific ID
        System.out.println("4. Item with ID '2':");
        List<TodoItem> item2Query = storage.executeQuery("SELECT * FROM todos WHERE id = '2'");
        item2Query.forEach(item ->
            System.out.printf("   %s: %s [%s]%n",
                item.getId(),
                item.getDescription(),
                item.isCompleted() ? "��✓" : "��✗"));
        System.out.println();

        // 5. Complex query with AND
        System.out.println("5. Completed items with ID '1' or '3':");
        List<TodoItem> complexQuery = storage.executeQuery("SELECT * FROM todos WHERE completed = true AND (id = '1' OR id = '3')");
        complexQuery.forEach(item ->
            System.out.printf("   %s: %s%n", item.getId(), item.getDescription()));
        System.out.println();

        // 6. NULL checks (completedAt is null for incomplete items)
        System.out.println("6. Items with null completedAt (incomplete):");
        List<TodoItem> nullCompletedAt = storage.executeQuery("SELECT * FROM todos WHERE completedAt IS NULL");
        nullCompletedAt.forEach(item ->
            System.out.printf("   %s: %s%n", item.getId(), item.getDescription()));
        System.out.println();

        // 7. NOT NULL checks (completedAt is not null for completed items)
        System.out.println("7. Items with non-null completedAt (completed):");
        List<TodoItem> notNullCompletedAt = storage.executeQuery("SELECT * FROM todos WHERE completedAt IS NOT NULL");
        notNullCompletedAt.forEach(item ->
            System.out.printf("   %s: %s%n", item.getId(), item.getDescription()));
        System.out.println();

        System.out.println("Demo complete! Storage saved to demo-todo.txt");
    }
}