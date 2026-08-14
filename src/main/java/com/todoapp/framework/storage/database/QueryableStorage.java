package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.StorageGateway;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * File-based storage implementation with SQL-like query capabilities.
 * Stores todo items in a text file with pipe-delimited format:
 * id|description|completed|createdAt|completedAt
 *
 * Provides basic querying functionality through the executeQuery method.
 */
public class QueryableStorage implements StorageGateway {
    private static final String DEFAULT_FILE_NAME = "todo.txt";
    private final String fileName;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
    private final QueryEngine queryEngine;

    public QueryableStorage() {
        this(DEFAULT_FILE_NAME);
    }

    public QueryableStorage(String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "File name cannot be null");
        this.queryEngine = new QueryEngine();
    }

    @Override
    public void save(List<TodoItem> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (TodoItem item : items) {
                String line = formatTodoItem(item);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save todo items to file: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TodoItem> load() {
        List<TodoItem> items = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            // Return empty list if file doesn't exist yet
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    TodoItem item = parseTodoItem(line);
                    if (item != null) {
                        items.add(item);
                    }
                } catch (IllegalArgumentException e) {
                    // Skip malformed lines but could log warning in real application
                    System.err.println("Warning: Skipping malformed line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load todo items from file: " + e.getMessage(), e);
        }

        // Load items into query engine for indexing
        queryEngine.loadItems(items);
        return items;
    }

    /**
     * Executes a SQL-like query on the stored todo items.
     *
     * Supported operations:
     * - SELECT * FROM todos WHERE [conditions]
     * - Returns list of matching TodoItem objects
     *
     * @param query SQL-like query string
     * @return List of matching TodoItem objects
     */
    public List<TodoItem> executeQuery(String query) {
        // Load current items from file
        List<TodoItem> currentItems = load();
        // Execute query against current items
        return queryEngine.executeQuery(query, currentItems);
    }

    /**
     * Adds a new todo item to the storage.
     *
     * @param item TodoItem to add
     */
    public void addItem(TodoItem item) {
        List<TodoItem> currentItems = load();
        currentItems.add(item);
        save(currentItems);
        // Reload items into query engine
        queryEngine.loadItems(currentItems);
    }

    /**
     * Updates an existing todo item in the storage.
     *
     * @param item TodoItem with updated values (must have matching ID)
     */
    public void updateItem(TodoItem item) {
        List<TodoItem> currentItems = load();
        boolean found = false;
        for (int i = 0; i < currentItems.size(); i++) {
            if (currentItems.get(i).getId().equals(item.getId())) {
                currentItems.set(i, item);
                found = true;
                break;
            }
        }
        if (found) {
            save(currentItems);
            // Reload items into query engine
            queryEngine.loadItems(currentItems);
        }
    }

    /**
     * Removes a todo item from the storage by ID.
     *
     * @param id ID of the item to remove
     */
    public void removeItem(String id) {
        List<TodoItem> currentItems = load();
        currentItems.removeIf(item -> item.getId().equals(id));
        save(currentItems);
        // Reload items into query engine
        queryEngine.loadItems(currentItems);
    }

    /**
     * Formats a TodoItem into a string for storage.
     *
     * @param item TodoItem to format
     * @return Formatted string representation
     */
    private String formatTodoItem(TodoItem item) {
        String completedAt = (item.getCompletedAt() != null)
                ? DATE_TIME_FORMATTER.format(item.getCompletedAt())
                : "";

        return String.join("|",
                item.getId(),
                item.getDescription(),
                Boolean.toString(item.isCompleted()),
                DATE_TIME_FORMATTER.format(item.getCreatedAt()),
                completedAt);
    }

    /**
     * Parses a string into a TodoItem.
     *
     * @param line String representation of a TodoItem
     * @return Parsed TodoItem
     * @throws IllegalArgumentException if the line is malformed
     */
    private TodoItem parseTodoItem(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|", -1); // -1 to keep trailing empty strings
        if (parts.length != 5) {
            throw new IllegalArgumentException("Expected 5 parts, got " + parts.length);
        }

        String id = parts[0].trim();
        String description = parts[1].trim();
        boolean completed = Boolean.parseBoolean(parts[2].trim());
        Instant createdAt = Instant.from(DATE_TIME_FORMATTER.parse(parts[3].trim()));
        Instant completedAt = parts[4].trim().isEmpty()
                ? null
                : Instant.from(DATE_TIME_FORMATTER.parse(parts[4].trim()));

        // Validate required fields
        if (id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        return new TodoItem(id, description, completed, createdAt, completedAt);
    }
}