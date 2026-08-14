package com.todoapp.framework.storage;

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

import com.todoapp.domain.TodoItem;

/**
 * File system implementation of StorageGateway.
 * Stores todo items in a text file with pipe-delimited format:
 * id|description|completed|createdAt|completedAt
 */
public class FileStorage implements StorageGateway {
    private static final String DEFAULT_FILE_NAME = "db/todo.txt";
    private final String fileName;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());

    public FileStorage() {
        this(DEFAULT_FILE_NAME);
    }

    public FileStorage(String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "File name cannot be null");
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

        return items;
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