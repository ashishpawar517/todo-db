package com.todoapp.framework.ui;

import com.todoapp.domain.TodoItem;

import java.util.List;
import java.util.Scanner;

/**
 * Terminal implementation of TodoListPresenter.
 * Provides an old Linux style interface inspired by classic Unix utilities.
 */
public class TerminalUI implements TodoListPresenter {
    private final Scanner scanner;
    private static final String ANSI_RESET = "[0m";
    private static final String ANSI_GREEN = "[32m";
    private static final String ANSI_RED = "[31m";
    private static final String ANSI_YELLOW = "[33m";
    private static final String ANSI_BLUE = "[34m";

    public TerminalUI() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayItems(List<TodoItem> items) {
        if (items.isEmpty()) {
            System.out.println(ANSI_YELLOW + "No todo items found." + ANSI_RESET);
            return;
        }

        System.out.println(ANSI_BLUE + "=== Todo List ===" + ANSI_RESET);
        System.out.printf("%-4s %-50s %-12s %-20s%n", "ID", "Description", "Status", "Created");
        System.out.println(ANSI_BLUE + "--------------------------------------------------" + ANSI_RESET);

        for (TodoItem item : items) {
            String status = item.isCompleted() ? "[DONE]" : "[TODO]";
            String statusColor = item.isCompleted() ? ANSI_GREEN : ANSI_RESET;

            String id = item.getId().length() > 3 ? item.getId().substring(0, 3) + "..." : item.getId();
            String description = item.getDescription().length() > 48
                ? item.getDescription().substring(0, 45) + "..."
                : item.getDescription();
            String created = item.getCreatedAt().toString().substring(0, 19).replace('T', ' ');

            System.out.printf("%-4s %-50s %-12s %-20s%n",
                id,
                description,
                statusColor + status + ANSI_RESET,
                created);
        }

        System.out.println(ANSI_BLUE + "--------------------------------------------------" + ANSI_RESET);
        System.out.println("Total: " + items.size() + " items");
    }

    @Override
    public void showError(String message) {
        System.err.println(ANSI_RED + "Error: " + message + ANSI_RESET);
    }

    @Override
    public void showSuccess(String message) {
        System.out.println(ANSI_GREEN + "Success: " + message + ANSI_RESET);
    }

    @Override
    public String promptForInput(String prompt) {
        System.out.print(ANSI_YELLOW + prompt + ": " + ANSI_RESET);
        System.out.flush(); // Ensure prompt is displayed before waiting for input
        return scanner.nextLine().trim();
    }
}