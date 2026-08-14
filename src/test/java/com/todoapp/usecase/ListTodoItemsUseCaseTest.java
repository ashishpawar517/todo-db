package com.todoapp.usecase;

import com.todoapp.domain.TodoItem;
import com.todoapp.domain.TodoList;
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
 * Unit tests for ListTodoItemsUseCase.
 */
@ExtendWith(MockitoExtension.class)
class ListTodoItemsUseCaseTest {

  @Mock
  private TodoList todoList;

  @Mock
  private TodoListPresenter presenter;

  @InjectMocks
  private ListTodoItemsUseCase useCase;

  @Test
  void testExecute_NoFilter_ShowsAllItems() {
    // Arrange
    TodoItem item1 = new TodoItem("1", "Item 1");
    TodoItem item2 = new TodoItem("2", "Item 2");
    List<TodoItem> allItems = Arrays.asList(item1, item2);
    when(todoList.getAllItems()).thenReturn(allItems);

    // Act
    useCase.execute(); // Default is "all" filter

    // Assert
    verify(todoList).getAllItems();
    verify(presenter).displayItems(allItems);
  }

  @Test
  void testExecute_WithAllFilter_ShowsAllItems() {
    // Arrange
    TodoItem item1 = new TodoItem("1", "Item 1");
    TodoItem item2 = new TodoItem("2", "Item 2");
    List<TodoItem> allItems = Arrays.asList(item1, item2);
    when(todoList.getAllItems()).thenReturn(allItems);

    // Act
    useCase.execute("all");

    // Assert
    verify(todoList).getAllItems();
    verify(presenter).displayItems(allItems);
  }

  @Test
  void testExecute_WithActiveFilter_ShowsActiveItems() {
    // Arrange
    TodoItem activeItem = new TodoItem("1", "Active Item");
    TodoItem completedItem = new TodoItem("2", "Completed Item");
    completedItem.markAsCompleted();
    List<TodoItem> allItems = Arrays.asList(activeItem, completedItem);
    List<TodoItem> activeItems = Arrays.asList(activeItem);
    when(todoList.getActiveItems()).thenReturn(activeItems);

    // Act
    useCase.execute("active");

    // Assert
    verify(todoList).getActiveItems();
    verify(presenter).displayItems(activeItems);
  }

  @Test
  void testExecute_WithCompletedFilter_ShowsCompletedItems() {
    // Arrange
    TodoItem activeItem = new TodoItem("1", "Active Item");
    TodoItem completedItem = new TodoItem("2", "Completed Item");
    completedItem.markAsCompleted();
    List<TodoItem> allItems = Arrays.asList(activeItem, completedItem);
    List<TodoItem> completedItems = Arrays.asList(completedItem);
    when(todoList.getCompletedItems()).thenReturn(completedItems);

    // Act
    useCase.execute("completed");

    // Assert
    verify(todoList).getCompletedItems();
    verify(presenter).displayItems(completedItems);
  }

  @Test
  void testExecute_WithUnknownFilter_DefaultsToAll() {
    // Arrange
    TodoItem item1 = new TodoItem("1", "Item 1");
    TodoItem item2 = new TodoItem("2", "Item 2");
    List<TodoItem> allItems = Arrays.asList(item1, item2);
    when(todoList.getAllItems()).thenReturn(allItems);

    // Act
    useCase.execute("unknown"); // Should default to "all"

    // Assert
    verify(todoList).getAllItems();
    verify(presenter).displayItems(allItems);
  }

  @Test
  void testExecute_WhenUseCaseThrowsException_ShowsError() {
    // Arrange
    when(todoList.getAllItems()).thenThrow(new RuntimeException("Storage error"));

    // Act
    useCase.execute();

    // Assert
    verify(todoList).getAllItems();
    verify(presenter, never()).displayItems(anyList());
    verify(presenter).showError("Failed to list todo items: Storage error");
  }
}