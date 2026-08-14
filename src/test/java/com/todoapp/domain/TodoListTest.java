package com.todoapp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TodoList entity.
 * Tests the collection management logic of todo items.
 */
class TodoListTest {

    private TodoList todoList;

    @BeforeEach
    void setUp() {
        todoList = new TodoList();
    }

    @Test
    void testCreation() {
        assertTrue(todoList.getAllItems().isEmpty());
        assertEquals(0, todoList.getItemCount());
    }

    @Test
    void testAddItem() {
        String description = "Test todo item";
        TodoItem item = todoList.addItem(description);

        assertNotNull(item.getId());
        assertEquals(description, item.getDescription());
        assertFalse(item.isCompleted());

        assertEquals(1, todoList.getItemCount());
        assertTrue(todoList.getAllItems().contains(item));
    }

    @Test
    void testAddMultipleItems() {
        TodoItem item1 = todoList.addItem("First item");
        TodoItem item2 = todoList.addItem("Second item");

        assertEquals(2, todoList.getItemCount());
        assertTrue(todoList.getAllItems().contains(item1));
        assertTrue(todoList.getAllItems().contains(item2));
        assertNotEquals(item1.getId(), item2.getId()); // IDs should be unique
    }

    @Test
    void testGetItem() {
        TodoItem item = todoList.addItem("Test item");
        TodoItem found = todoList.getItem(item.getId());

        assertNotNull(found);
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getDescription(), found.getDescription());
    }

    @Test
    void testGetItemNotFound() {
        TodoItem found = todoList.getItem("non-existent-id");
        assertNull(found);
    }

    @Test
    void testRemoveItem() {
        TodoItem item = todoList.addItem("Test item");
        boolean removed = todoList.removeItem(item.getId());

        assertTrue(removed);
        assertTrue(todoList.getAllItems().isEmpty());
        assertEquals(0, todoList.getItemCount());
    }

    @Test
    void testRemoveItemNotFound() {
        boolean removed = todoList.removeItem("non-existent-id");
        assertFalse(removed);
    }

    @Test
    void testCompleteItem() {
        TodoItem item = todoList.addItem("Test item");
        boolean completed = todoList.completeItem(item.getId());

        assertTrue(completed);
        assertTrue(item.isCompleted());
        assertNotNull(item.getCompletedAt());
    }

    @Test
    void testCompleteItemNotFound() {
        boolean completed = todoList.completeItem("non-existent-id");
        assertFalse(completed);
    }

    @Test
    void testIncompleteItem() {
        TodoItem item = todoList.addItem("Test item");
        // First complete it
        todoList.completeItem(item.getId());
        assertTrue(item.isCompleted());

        // Then mark as incomplete
        boolean incomplete = todoList.incompleteItem(item.getId());
        assertTrue(incomplete);
        assertFalse(item.isCompleted());
        assertNull(item.getCompletedAt());
    }

    @Test
    void testToggleItem() {
        TodoItem item = todoList.addItem("Test item");
        assertFalse(item.isCompleted());

        boolean toggled = todoList.toggleItem(item.getId());
        assertTrue(toggled);
        assertTrue(item.isCompleted());

        toggled = todoList.toggleItem(item.getId());
        assertTrue(toggled);
        assertFalse(item.isCompleted());
    }

    @Test
    void testGetActiveItems() {
        TodoItem active1 = todoList.addItem("Active item 1");
        TodoItem active2 = todoList.addItem("Active item 2");
        TodoItem completed = todoList.addItem("Completed item");

        // Complete one item
        todoList.completeItem(completed.getId());

        List<TodoItem> activeItems = todoList.getActiveItems();
        assertEquals(2, activeItems.size());
        assertTrue(activeItems.contains(active1));
        assertTrue(activeItems.contains(active2));
        assertFalse(activeItems.contains(completed));
    }

    @Test
    void testGetCompletedItems() {
        TodoItem active1 = todoList.addItem("Active item 1");
        TodoItem active2 = todoList.addItem("Active item 2");
        TodoItem completed1 = todoList.addItem("Completed item 1");
        TodoItem completed2 = todoList.addItem("Completed item 2");

        // Complete two items
        todoList.completeItem(completed1.getId());
        todoList.completeItem(completed2.getId());

        List<TodoItem> completedItems = todoList.getCompletedItems();
        assertEquals(2, completedItems.size());
        assertTrue(completedItems.contains(completed1));
        assertTrue(completedItems.contains(completed2));
        assertFalse(completedItems.contains(active1));
        assertFalse(completedItems.contains(active2));
    }

    @Test
    void testGetAllItems() {
        TodoItem item1 = todoList.addItem("Item 1");
        TodoItem item2 = todoList.addItem("Item 2");

        List<TodoItem> allItems = todoList.getAllItems();
        assertEquals(2, allItems.size());
        assertTrue(allItems.contains(item1));
        assertTrue(allItems.contains(item2));
    }

    @Test
    void testConstructorWithNullItems() {
        assertThrows(NullPointerException.class, () -> {
            new TodoList(null);
        });
    }

    @Test
    void testItemIdsAreUnique() {
        // Add many items and verify all IDs are unique
        java.util.Set<String> ids = new java.util.HashSet<>();
        int numberOfItems = 100;

        for (int i = 0; i < numberOfItems; i++) {
            TodoItem item = todoList.addItem("Item " + i);
            assertTrue(ids.add(item.getId()), "Duplicate ID found: " + item.getId());
        }

        assertEquals(numberOfItems, todoList.getItemCount());
    }
}