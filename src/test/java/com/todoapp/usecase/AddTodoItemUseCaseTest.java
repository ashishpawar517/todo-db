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
 * Unit tests for AddTodoItemUseCase.
 */
@ExtendWith(MockitoExtension.class)
class AddTodoItemUseCaseTest {

  @Mock
  private TodoList todoList;

  @Mock
  private StorageGateway storageGateway;

  @Mock
  private TodoListPresenter presenter;

  @InjectMocks
  private AddTodoItemUseCase useCase;

  @Test
  void testExecute_WithValidDescription_AddsItemAndShowsSuccess() {
    // Arrange
    String description = "Test todo item";
    TodoItem expectedItem = new TodoItem("test-id", description);
    when(todoList.addItem(description)).thenReturn(expectedItem);
    when(todoList.getAllItems()).thenReturn(Arrays.asList(expectedItem));

    // Act
    useCase.execute(description);

    // Assert
    verify(todoList).addItem(description);
    verify(storageGateway).save(Arrays.asList(expectedItem));
    verify(presenter).showSuccess("Added: " + description);
  }

  @Test
  void testExecute_WithEmptyDescription_ShowsError() {
    // Act
    useCase.execute("");

    // Assert
    verify(todoList, never()).addItem(anyString());
    verify(storageGateway, never()).save(anyList());
    verify(presenter).showError("Please provide a description for the todo item.");
  }

  @Test
  void testExecute_WithNullDescription_ShowsError() {
    // Act
    useCase.execute(null);

    // Assert
    verify(todoList, never()).addItem(anyString());
    verify(storageGateway, never()).save(anyList());
    verify(presenter).showError("Please provide a description for the todo item.");
  }

  @Test
  void testExecute_WhenUseCaseThrowsException_ShowsError() {
    // Arrange
    String description = "Test todo item";
    when(todoList.addItem(description)).thenThrow(new RuntimeException("Database error"));

    // Act
    useCase.execute(description);

    // Assert
    verify(todoList).addItem(description);
    verify(storageGateway, never()).save(anyList());
    verify(presenter).showError("Failed to add todo item: Database error");
  }
}