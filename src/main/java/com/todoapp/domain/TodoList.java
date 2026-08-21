package com.todoapp.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages a collection of todo items.
 * This is an entity that contains and manages TodoItem objects.
 */
public class TodoList {
    private final List<TodoItem> items;

    public TodoList() {
        this.items = new ArrayList<>();
    }

    public TodoList(List<TodoItem> items) {
        this.items = Objects.requireNonNull(items, "Items list cannot be null");
    }

    /**
     * Adds a new todo item with the given description.
     *
     * @param description Description of the new todo item
     * @return The newly created TodoItem
     */
    public TodoItem addItem(String description) {
        String id = generateId();
        TodoItem item = new TodoItem(id, description);
        items.add(item);
        return item;
    }

    /**
     * Generates a dynamic ID based on the number of items in the list.
     * If item count < 2: 2-character ID
     * If item count > 8: 3-character ID
     * Otherwise: 2-character ID
     * Uses base-36 encoding (0-9, a-z) for compact representation.
     *
     * @return Generated unique ID
     */
    private String generateId() {
        int itemCount = items.size();
        int idLength;

        if (itemCount < 2) {
            idLength = 2;
        } else if (itemCount > 8) {
            idLength = 3;
        } else {
            idLength = 2;
        }

        // Generate a random ID of specified length using base-36 characters
        StringBuilder idBuilder = new StringBuilder(idLength);
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < idLength; i++) {
            int randomIndex = random.nextInt(chars.length());
            idBuilder.append(chars.charAt(randomIndex));
        }

        String generatedId = idBuilder.toString();

        // Ensure uniqueness by checking against existing IDs
        // If collision occurs (unlikely but possible), append a counter
        int counter = 0;
        String originalId = generatedId;
        while (items.stream().anyMatch(item -> item.getId().equals(generatedId))) {
            counter++;
            // If we've tried too many times, fall back to UUID
            if (counter > 1000) {
                return UUID.randomUUID().toString();
            }
            // Append counter to maintain length while ensuring uniqueness
            if (idLength == 2) {
                generatedId = String.format("%-2s", originalId + counter).replace(' ', '0');
                if (generatedId.length() > 2) {
                    generatedId = generatedId.substring(0, 2);
                }
            } else {
                generatedId = String.format("%-3s", originalId + counter).replace(' ', '0');
                if (generatedId.length() > 3) {
                    generatedId = generatedId.substring(0, 3);
                }
            }
        }

        return generatedId;
    }

    /**
     * Removes a todo item by its ID.
     *
     * @param id ID of the item to remove
     * @return true if item was found and removed, false otherwise
     */
    public boolean removeItem(String id) {
        return items.removeIf(item -> item.getId().equals(id));
    }

    /**
     * Gets a todo item by its ID.
     *
     * @param id ID of the item to retrieve
     * @return The TodoItem if found, null otherwise
     */
    public TodoItem getItem(String id) {
        return items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Marks a todo item as completed by its ID.
     *
     * @param id ID of the item to mark as completed
     * @return true if item was found and marked, false otherwise
     */
    public boolean completeItem(String id) {
        TodoItem item = getItem(id);
        if (item != null) {
            item.markAsCompleted();
            return true;
        }
        return false;
    }

    /**
     * Marks a todo item as incomplete by its ID.
     *
     * @param id ID of the item to mark as incomplete
     * @return true if item was found and marked, false otherwise
     */
    public boolean incompleteItem(String id) {
        TodoItem item = getItem(id);
        if (item != null) {
            item.markAsIncomplete();
            return true;
        }
        return false;
    }

    /**
     * Toggles the completion status of a todo item by its ID.
     *
     * @param id ID of the item to toggle
     * @return true if item was found and toggled, false otherwise
     */
    public boolean toggleItem(String id) {
        TodoItem item = getItem(id);
        if (item != null) {
            item.toggleCompletion();
            return true;
        }
        return false;
    }

    /**
     * Gets all todo items.
     *
     * @return Unmodifiable list of all todo items
     */
    public List<TodoItem> getAllItems() {
        return List.copyOf(items);
    }

    /**
     * Gets all active (incomplete) todo items.
     *
     * @return List of active todo items
     */
    public List<TodoItem> getActiveItems() {
        return items.stream()
                .filter(item -> !item.isCompleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets all completed todo items.
     *
     * @return List of completed todo items
     */
    public List<TodoItem> getCompletedItems() {
        return items.stream()
                .filter(TodoItem::isCompleted)
                .collect(Collectors.toList());
    }

    /**
     * Gets the count of all todo items.
     *
     * @return Number of todo items
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Gets the count of active (incomplete) todo items.
     *
     * @return Number of active todo items
     */
    public int getActiveItemCount() {
        return (int) items.stream()
                .filter(item -> !item.isCompleted())
                .count();
    }

    /**
     * Gets the count of completed todo items.
     *
     * @return Number of completed todo items
     */
    public int getCompletedItemCount() {
        return (int) items.stream()
                .filter(TodoItem::isCompleted)
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoList todoList = (TodoList) o;
        return items.equals(todoList.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return "TodoList{" +
                "items=" + items +
                '}';
    }
}