package com.todoapp.usecase;

import com.todoapp.domain.TodoItem;
import com.todoapp.domain.TodoList;
import com.todoapp.framework.storage.StorageGateway;
import com.todoapp.framework.ui.TodoListPresenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ToggleTodoItemUseCase.
 */
@ExtendWith(MockitoExtension.class)
class ToggleTodoItemUseCaseTest {

  @Mock
  private TodoList todoList;

  @Mock
  private StorageGateway storageGateway;

  @Mock
  private TodoListPresenter presenter;

  @InjectMocks
  private ToggleTodoItemUseCase useCase;

  @Test
  void testExecute_WithIncompleteItem_MarksAsCompletedAndShowsSuccess() {
    // Arrange
    String id = "test-id";
    String description = "Test todo item";
    TodoItem item = new TodoItem(id, description);
    // Item starts as incomplete
    when(todoList.getItem(id)).thenReturn(item);
    // Make the mock actually toggle the item when called
    doAnswer(invocation -> {
      item.toggleCompletion(); // Actually toggle the item
      return true;
    }).when(todoList).toggleItem(id);
    when(todoList.getAllItems()).thenReturn(Arrays.asList(item));

    // Act
    useCase.execute(id);

    // Assert
    verify(todoList).getItem(id);
    verify(todoList).toggleItem(id);
    verify(storageGateway).save(Arrays.asList(item));
    verify(presenter).showSuccess("Item '" + description + "' completed.");
  }

  @Test
  void testExecute_WithCompletedItem_MarksAsIncompleteAndShowsSuccess() {
    // Arrange
    String id = "test-id";
    String description = "Test todo item";
    TodoItem item = new TodoItem(id, description);
    // Mark item as completed initially
    item.markAsCompleted();
    when(todoList.getItem(id)).thenReturn(item);
    // Make the mock actually toggle the item when called
    doAnswer(invocation -> {
      item.toggleCompletion(); // Actually toggle the item
      return true;
    }).when(todoList).toggleItem(id);
    when(todoList.getAllItems()).thenReturn(Arrays.asList(item));

    // Act
    useCase.execute(id);

    // Assert
    verify(todoList).getItem(id);
    verify(todoList).toggleItem(id);
    verify(storageGateway).save(Arrays.asList(item));
    verify(presenter).showSuccess("Item '" + description + "' activated.");
  }

  @Test
  void testExecute_WithNonExistingItem_ShowsError() {
    // Arrange
    String id = "non-existent-id";
    when(todoList.getItem(id)).thenReturn(null);

    // Act
    useCase.execute(id);

    // Assert
    verify(todoList).getItem(id);
    verify(todoList, never()).toggleItem(anyString());
    verify(storageGateway, never()).save(anyList());
    verify(presenter).showError("Todo item with ID '" + id + "' not found.");
  }

  @Test
  void testExecute_WithEmptyId_ShowsError() {
    // Act
    useCase.execute("");

    // Assert
    verify(presenter).showError("Please provide an item ID to mark as completed.");
  }

  @Test
  void testExecute_WhenUseCaseThrowsException_ShowsError() {
    // Arrange
    String id = "test-id";
    when(todoList.getItem(id)).thenThrow(new RuntimeException("Toggle error"));

    // Act
    useCase.execute(id);

    // Assert
    verify(todoList).getItem(id);
    verify(todoList, never()).toggleItem(anyString());
    verify(storageGateway, never()).save(anyList());
    verify(presenter).showError("Failed to toggle todo item: Toggle error");
  }
}