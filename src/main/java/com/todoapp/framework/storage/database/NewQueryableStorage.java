package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;
import com.todoapp.framework.storage.StorageGateway;
import com.todoapp.framework.sql.SQLEngine;
import com.todoapp.framework.sql.SQLParser;
import com.todoapp.framework.sql.QueryExecutor;
import com.todoapp.framework.sql.BasicSQLParser;
import com.todoapp.framework.sql.BasicQueryExecutor;

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
import java.util.function.Function;

/**
 * New implementation of QueryableStorage that uses the modular SQL engine.
 * This demonstrates how the storage layer can integrate with the new SQL parser
 * and executor interfaces while maintaining the StorageGateway contract.
 *
 * This class is functionally equivalent to the original QueryableStorage but
 * uses the new modular SQL engine design with separate parsing and execution
 * concerns.
 */
public class NewQueryableStorage implements StorageGateway {

    private static final String DEFAULT_FILE_NAME = "db/todo.txt";
    private final String fileName;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
    private final SQLEngine sqlEngine;
    private List<TodoItem> cachedItems;

    public NewQueryableStorage() {
        this(DEFAULT_FILE_NAME);
    }

    public NewQueryableStorage(String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "File name cannot be null");

        // Create the modular SQL engine components
        SQLParser parser = new BasicSQLParser();
        QueryExecutor executor = new BasicQueryExecutor();
        this.sqlEngine = new SQLEngine(parser, executor);

        this.cachedItems = new ArrayList<>();
        // Load initial items
        loadItemsIntoCache();
    }

    @Override
    public void save(List<TodoItem> items) {
        // Ensure parent directory exists
        File file = new File(fileName);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean dirsCreated = parentDir.mkdirs();
            if (!dirsCreated) {
                throw new RuntimeException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

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

        // Load items into SQL engine for querying
        sqlEngine.executeUpdate("LOAD", items); // Dummy call to load items - in a real implementation,
                                              // we might have a proper loadItems method on the engine
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
        // Execute query against cached items to avoid redundant I/O
        return sqlEngine.executeQuery(query, cachedItems);
    }

    /**
     * Executes a SQL-like update (INSERT, UPDATE, DELETE) on the stored todo items.
     * Modifies the cached items and persists to storage.
     *
     * @param query SQL-like query string (INSERT, UPDATE, DELETE)
     * @return Number of rows affected
     */
    public int executeUpdate(String query) {
        // Execute update against cached items
        int affectedRows = sqlEngine.executeUpdate(query, cachedItems);

        // If the update modified the data, persist to storage
        if (affectedRows != 0) {
            save(cachedItems);
        }

        return affectedRows;
    }

    /**
     * Adds a new todo item to the storage.
     *
     * @param item TodoItem to add
     */
    public void addItem(TodoItem item) {
        updateCachedItems(items -> {
            List<TodoItem> updatedItems = new ArrayList<>(items);
            updatedItems.add(item);
            return updatedItems;
        });
    }

    /**
     * Updates an existing todo item in the storage.
     *
     * @param item TodoItem with updated values (must have matching ID)
     */
    public void updateItem(TodoItem item) {
        updateCachedItems(items -> {
            List<TodoItem> updatedItems = new ArrayList<>(items);
            boolean found = false;
            for (int i = 0; i < updatedItems.size(); i++) {
                if (updatedItems.get(i).getId().equals(item.getId())) {
                    updatedItems.set(i, item);
                    found = true;
                    break;
                }
            }
            return found ? updatedItems : items;
        });
    }

    /**
     * Removes a todo item from the storage by ID.
     *
     * @param id ID of the item to remove
     */
    public void removeItem(String id) {
        updateCachedItems(items -> {
            List<TodoItem> updatedItems = new ArrayList<>(items);
            updatedItems.removeIf(item -> item.getId().equals(id));
            return updatedItems;
        });
    }

    /**
     * Updates the cached items and persists to storage.
     *
     * @param updater Function that takes current items and returns updated items
     */
    private void updateCachedItems(Function<List<TodoItem>, List<TodoItem>> updater) {
        List<TodoItem> currentItems = new ArrayList<>(cachedItems);
        List<TodoItem> updatedItems = updater.apply(currentItems);

        if (!updatedItems.equals(currentItems)) {
            save(updatedItems);
            cachedItems = updatedItems;
            // Note: In a more advanced implementation, the SQLEngine might
            // have a way to notify it that the underlying data has changed
        }
    }

    /**
     * Loads items from file into the cache.
     * Call this when the storage needs to be refreshed from disk.
     */
    public void loadItemsIntoCache() {
        cachedItems = load();
        // In a more advanced implementation, we would notify the SQL engine
        // that the cached data has been refreshed
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