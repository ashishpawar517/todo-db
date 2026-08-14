package com.todoapp.framework.ui;

import com.todoapp.domain.TodoItem;

import java.util.List;

/**
 * Interface for presenting todo items to the user.
 * Follows Dependency Inversion Principle - high-level modules depend on abstractions.
 */
public interface TodoListPresenter {
    /**
     * Displays a list of todo items to the user.
     *
     * @param items List of todo items to display
     */
    void displayItems(List<TodoItem> items);

    /**
     * Shows an error message to the user.
     *
     * @param message Error message to display
     */
    void showError(String message);

    /**
     * Shows a success message to the user.
     *
     * @param message Success message to display
     */
    void showSuccess(String message);

    /**
     * Prompts the user for input.
     *
     * @param prompt Message to display to prompt for input
     * @return User input as a string
     */
    String promptForInput(String prompt);
}