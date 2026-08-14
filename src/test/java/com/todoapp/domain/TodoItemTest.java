package com.todoapp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TodoItem entity.
 * Tests the core business logic of a todo item.
 */
class TodoItemTest {

    private TodoItem todoItem;

    @BeforeEach
    void setUp() {
        todoItem = new TodoItem("test-id", "Test description");
    }

    @Test
    void testCreation() {
        assertEquals("test-id", todoItem.getId());
        assertEquals("Test description", todoItem.getDescription());
        assertFalse(todoItem.isCompleted());
        assertNotNull(todoItem.getCreatedAt());
        assertNull(todoItem.getCompletedAt());
    }

    @Test
    void testMarkAsCompleted() {
        todoItem.markAsCompleted();
        assertTrue(todoItem.isCompleted());
        assertNotNull(todoItem.getCompletedAt());
        // completedAt should be after or equal to createdAt
        assertTrue(todoItem.getCompletedAt().isAfter(todoItem.getCreatedAt()) ||
                todoItem.getCompletedAt().equals(todoItem.getCreatedAt()));
    }

    @Test
    void testMarkAsIncomplete() {
        // First mark as completed
        todoItem.markAsCompleted();
        assertTrue(todoItem.isCompleted());

        // Then mark as incomplete
        todoItem.markAsIncomplete();
        assertFalse(todoItem.isCompleted());
        assertNull(todoItem.getCompletedAt());
    }

    @Test
    void testToggleCompletion() {
        assertFalse(todoItem.isCompleted());

        todoItem.toggleCompletion();
        assertTrue(todoItem.isCompleted());
        assertNotNull(todoItem.getCompletedAt());

        todoItem.toggleCompletion();
        assertFalse(todoItem.isCompleted());
        assertNull(todoItem.getCompletedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        TodoItem sameItem = new TodoItem("test-id", "Different description");
        TodoItem differentItem = new TodoItem("different-id", "Test description");

        assertEquals(todoItem, sameItem);
        assertNotEquals(todoItem, differentItem);
        assertEquals(todoItem.hashCode(), sameItem.hashCode());
    }

    @Test
    void testConstructorWithNullValues() {
        assertThrows(NullPointerException.class, () -> {
            new TodoItem(null, "Test description");
        });

        assertThrows(NullPointerException.class, () -> {
            new TodoItem("test-id", null);
        });

        assertThrows(NullPointerException.class, () -> {
            new TodoItem("test-id", "Test description", true, null, Instant.now());
        });

        // completedAt can be null (for incomplete items), so this should NOT throw
        assertDoesNotThrow(() -> {
            new TodoItem("test-id", "Test description", false, Instant.now(), null);
        });

        // But if we mark it as completed, completedAt should not be null
        TodoItem item = new TodoItem("test-id", "Test description", false, Instant.now(), null);
        item.markAsCompleted();
        assertNotNull(item.getCompletedAt());
    }
}