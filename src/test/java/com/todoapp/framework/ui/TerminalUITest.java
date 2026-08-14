package com.todoapp.framework.ui;

import com.todoapp.domain.TodoItem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TerminalUI.
 */
class TerminalUITest {

  private TerminalUI terminalUI;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUp() {
    terminalUI = new TerminalUI();
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  void testDisplayItemsEmptyList() {
    // Arrange
    List<TodoItem> items = new ArrayList<>();

    // Act
    terminalUI.displayItems(items);

    // Assert
    assertTrue(outContent.toString().contains("No todo items found."));
  }

  @Test
  void testDisplayItemsWithItems() {
    // Arrange
    TodoItem item1 = new TodoItem("abc123", "First item");
    TodoItem item2 = new TodoItem("def456", "Second item");
    item2.markAsCompleted();
    List<TodoItem> items = new ArrayList<>();
    items.add(item1);
    items.add(item2);

    // Act
    terminalUI.displayItems(items);

    // Assert
    String output = outContent.toString();
    // Not asserting on exact format due to potential spacing/format variations
    // Just checking that key elements are present
    assertTrue(output.contains("=== Todo List ==="), "Should contain header");
    assertTrue(output.contains("First item"), "Should contain first item description");
    assertTrue(output.contains("Second item"), "Should contain second item description");
    assertTrue(output.contains("[TODO]"), "Should contain TODO status");
    assertTrue(output.contains("[DONE]"), "Should contain DONE status");
  }

  @Test
  void testShowError() {
    // Arrange
    String errorMessage = "Test error message";

    // Act
    terminalUI.showError(errorMessage);

    // Assert
    assertTrue(errContent.toString().contains("Error: " + errorMessage));
  }

  @Test
  void testShowSuccess() {
    // Arrange
    String successMessage = "Test success message";

    // Act
    terminalUI.showSuccess(successMessage);

    // Assert
    assertTrue(outContent.toString().contains("Success: " + successMessage));
  }

  // Note: Testing promptForInput requires mocking Scanner which is more complex
  // For simplicity in this example, we're not testing the input method directly
  // In a production application, we might refactor to make it more testable
}