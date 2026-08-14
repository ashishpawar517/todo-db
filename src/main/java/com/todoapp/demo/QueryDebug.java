package com.todoapp.demo;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.database.QueryableStorage;

import java.util.List;

/**
 * Debug class to test specific query issues.
 */
public class QueryDebug {
    public static void main(String[] args) {
        QueryableStorage storage = new QueryableStorage("debug-todo.txt");

        // Add test data
        TodoItem item1 = new TodoItem("1", "Buy groceries");
        item1.markAsCompleted();
        TodoItem item2 = new TodoItem("2", "Walk the dog");
        TodoItem item3 = new TodoItem("3", "Write report");
        item3.markAsCompleted();

        storage.addItem(item1);
        storage.addItem(item2);
        storage.addItem(item3);

        System.out.println("Testing problematic query:");

        // Test the query that was failing in the demo
        System.out.println("\nQuery: SELECT * FROM todos WHERE completed = true AND (id = '1' OR id = '3')");
        List<TodoItem> result1 = storage.executeQuery("SELECT * FROM todos WHERE completed = true AND (id = '1' OR id = '3')");
        System.out.println("Results: " + result1.size());
        result1.forEach(item -> System.out.println("  - " + item.getId() + ": " + item.getDescription()));

        // Test equivalent query without parentheses
        System.out.println("\nQuery: SELECT * FROM todos WHERE completed = true AND id = '1' OR id = '3'");
        List<TodoItem> result2 = storage.executeQuery("SELECT * FROM todos WHERE completed = true AND id = '1' OR id = '3'");
        System.out.println("Results: " + result2.size());
        result2.forEach(item -> System.out.println("  - " + item.getId() + ": " + item.getDescription()));

        // Test completed = true alone
        System.out.println("\nQuery: SELECT * FROM todos WHERE completed = true");
        List<TodoItem> result3 = storage.executeQuery("SELECT * FROM todos WHERE completed = true");
        System.out.println("Results: " + result3.size());
        result3.forEach(item -> System.out.println("  - " + item.getId() + ": " + item.getDescription()));

        // Test id = '1' OR id = '3' alone
        System.out.println("\nQuery: SELECT * FROM todos WHERE id = '1' OR id = '3'");
        List<TodoItem> result4 = storage.executeQuery("SELECT * FROM todos WHERE id = '1' OR id = '3'");
        System.out.println("Results: " + result4.size());
        result4.forEach(item -> System.out.println("  - " + item.getId() + ": " + item.getDescription()));
    }
}