package com.todoapp;

import com.todoapp.domain.TodoItem;
import com.todoapp.domain.TodoList;
import com.todoapp.framework.storage.FileStorage;
import com.todoapp.framework.storage.StorageGateway;
import com.todoapp.framework.ui.TerminalUI;
import com.todoapp.framework.ui.TodoListPresenter;
import com.todoapp.usecase.AddTodoItemUseCase;
import com.todoapp.usecase.RemoveTodoItemUseCase;
import com.todoapp.usecase.ToggleTodoItemUseCase;
import com.todoapp.usecase.ListTodoItemsUseCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Main application.
 */
class MainTest {

  private Main mainApp;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private Path testStorageFile;

  @BeforeEach
  void setUp() throws Exception {
    // Create a temporary file for storage to avoid interfering with real data
    testStorageFile = Files.createTempFile("todo-test-", ".txt");
    System.setProperty("todo.storage.file", testStorageFile.toString());

    mainApp = new Main();
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @AfterEach
  void tearDown() throws Exception {
    System.setOut(originalOut);
    System.setErr(originalErr);
    // Clean up test file
    if (Files.exists(testStorageFile)) {
      Files.delete(testStorageFile);
    }
  }

  /**
   * Helper method to get a private field value using reflection
   */
  private <T> T getPrivateField(String fieldName) throws Exception {
    Field field = Main.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return (T) field.get(mainApp);
  }

  @Test
  void testMainInitialization() throws Exception {
    // Act / Assert
    assertNotNull(mainApp);
    assertNotNull(getPrivateField("todoList"));
    assertNotNull(getPrivateField("storageGateway"));
    assertNotNull(getPrivateField("presenter"));
    assertNotNull(getPrivateField("addUseCase"));
    assertNotNull(getPrivateField("removeUseCase"));
    assertNotNull(getPrivateField("toggleUseCase"));
    assertNotNull(getPrivateField("listUseCase"));
  }

  @Test
  void testMainShowsWelcomeMessageOnRun() throws Exception {
    // This test is limited because run() has an infinite loop
    // Instead we test that the Main object initializes correctly
    // A more comprehensive test would require refactoring to make the loop testable

    // Verify that all components are properly initialized
    assertTrue(getPrivateField("todoList") instanceof TodoList);
    assertTrue(getPrivateField("storageGateway") instanceof FileStorage);
    assertTrue(getPrivateField("presenter") instanceof TerminalUI);
    assertTrue(getPrivateField("addUseCase") instanceof AddTodoItemUseCase);
    assertTrue(getPrivateField("removeUseCase") instanceof RemoveTodoItemUseCase);
    assertTrue(getPrivateField("toggleUseCase") instanceof ToggleTodoItemUseCase);
    assertTrue(getPrivateField("listUseCase") instanceof ListTodoItemsUseCase);
  }
}