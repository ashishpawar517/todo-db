package com.todoapp.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a single todo item in the system.
 * This is an entity with identity and lifecycle.
 */
public class TodoItem {
    private final String id;
    private final String description;
    private boolean completed;
    private final Instant createdAt;
    private Instant completedAt;

    /**
     * Creates a new todo item.
     *
     * @param id          Unique identifier for the item
     * @param description Description of the task
     */
    public TodoItem(String id, String description) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.completed = false;
        this.createdAt = Instant.now();
        this.completedAt = null;
    }

    /**
     * Creates a new todo item with specified timestamps (for reconstruction from storage).
     *
     * @param id          Unique identifier
     * @param description Description of the task
     * @param completed   Completion status
     * @param createdAt   Creation timestamp
     * @param completedAt Completion timestamp (null if not completed)
     */
    public TodoItem(String id, String description, boolean completed, Instant createdAt, Instant completedAt) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.completed = completed;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.completedAt = completedAt; // completedAt can be null (for incomplete items)
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    /**
     * Marks this todo item as completed.
     */
    public void markAsCompleted() {
        this.completed = true;
        this.completedAt = Instant.now();
    }

    /**
     * Marks this todo item as incomplete.
     */
    public void markAsIncomplete() {
        this.completed = false;
        this.completedAt = null;
    }

    /**
     * Toggles the completion status of this todo item.
     */
    public void toggleCompletion() {
        if (completed) {
            markAsIncomplete();
        } else {
            markAsCompleted();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
        return id.equals(todoItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                '}';
    }
}