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
 * Unit tests for RemoveTodoItemUseCase.
 */
@ExtendWith(MockitoExtension.class)
class RemoveTodoItemUseCaseTest {

  @Mock
  private TodoList todoList;

  @Mock
  private StorageGateway storageGateway;

  @Mock
  private TodoListPresenter presenter;

  @InjectMocks
  private RemoveTodoItemUseCase useCase;

  @Test
  void testExecute_WithExistingItem_RemovesItemAndShowsSuccess() {
    // Arrange
    String id = "test-id";
    String description = "Test todo item";
    TodoItem item = new TodoItem(id, description);
    when(todoList.getItem(id)).thenReturn(item);
    when(todoList.removeItem(id)).thenReturn(true);
    when(todoList.getAllItems()).thenReturn(Arrays.asList());

    // Act
    useCase.execute(id);

    // Assert
    verify(todoList).getItem(id);
    verify(todoList).removeItem(id);
    verify(storageGateway).save(Arrays.asList());
    verify(presenter).showSuccess("Removed: " + description);
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
    verify(todoList, never()).removeItem(anyString());
    verify(storageGateway, never()).save(anyList());
    verify(presenter).showError("Todo item with ID '" + id + "' not found.");
  }

  @Test
  void testExecute_WithEmptyId_ShowsError() {
    // Act
    useCase.execute("");

    // Assert
    verify(presenter).showError("Please provide an item ID to remove.");
  }

  @Test
  void testExecute_WhenUseCaseThrowsException_ShowsError() {
    // Arrange
    String id = "test-id";
    when(todoList.getItem(id)).thenThrow(new RuntimeException("Storage error"));

    // Act
    useCase.execute(id);

    // Assert
    verify(todoList).getItem(id);
    verify(todoList, never()).removeItem(anyString());
    verify(storageGateway, never()).save(anyList());
    verify(presenter).showError("Failed to remove todo item: Storage error");
  }
}