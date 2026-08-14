package com.todoapp.framework.storage;

import com.todoapp.domain.TodoItem;

import java.util.List;

/**
 * Interface for storage operations.
 * Follows Dependency Inversion Principle - high-level modules depend on abstractions.
 */
public interface StorageGateway {
    /**
     * Saves the list of todo items to storage.
     *
     * @param items List of todo items to save
     */
    void save(List<TodoItem> items);

    /**
     * Loads the list of todo items from storage.
     *
     * @return List of todo items loaded from storage
     */
    List<TodoItem> load();
}