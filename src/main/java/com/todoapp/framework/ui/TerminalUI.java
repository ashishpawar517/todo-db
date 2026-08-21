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

        // Calculate column widths based on content and headers
        int idWidth = Math.max("ID".length(),
                              items.stream().mapToInt(i -> i.getId().length()).max().orElse(0));
        int descWidth = Math.max("Description".length(),
                                items.stream().mapToInt(i -> i.getDescription().length()).max().orElse(0));
        // Cap description width at 50 to prevent extremely wide tables
        if (descWidth > 50) descWidth = 50;
        int statusWidth = Math.max("Status".length(),
                                  items.stream().mapToInt(i ->
                                       i.isCompleted() ? "[DONE]".length() : "[TODO]".length()).max().orElse(0));
        int createdWidth = Math.max("Created".length(),
                                   items.stream().mapToInt(i ->
                                       i.getCreatedAt().toString().substring(0, 19).replace('T', ' ').length()).max().orElse(0));

        // Create format strings
        String headerFormat = "| %-" + idWidth + "s | %-" + descWidth + "s | %-" + statusWidth + "s | %-" + createdWidth + "s |%n";
        String separatorFormat = "+-%s-+-%s-+-%s-+-%s-+%n"
            .formatted("-".repeat(idWidth), "-".repeat(descWidth), "-".repeat(statusWidth), "-".repeat(createdWidth));

        // Print header
        System.out.printf(separatorFormat);
        System.out.printf(headerFormat, "ID", "Description", "Status", "Created");
        System.out.printf(separatorFormat);

        // Print rows
        for (TodoItem item : items) {
            String status = item.isCompleted() ? "[DONE]" : "[TODO]";
            String statusColor = item.isCompleted() ? ANSI_GREEN : ANSI_RESET;

            String id = item.getId();
            String description = item.getDescription();
            // Truncate description if too long for column
            if (description.length() > descWidth) {
                description = description.substring(0, descWidth - 3) + "...";
            }
            String created = item.getCreatedAt().toString().substring(0, 19).replace('T', ' ');

            System.out.printf("| %-" + idWidth + "s | %-" + descWidth + "s | %-" + statusWidth + "s | %-" + createdWidth + "s |%n",
                id,
                description,
                statusColor + status + ANSI_RESET,
                created);
        }

        // Print footer
        System.out.printf(separatorFormat);
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