package com.todoapp;

import com.todoapp.domain.TodoList;
import com.todoapp.framework.storage.FileStorage;
import com.todoapp.framework.storage.StorageGateway;
import com.todoapp.framework.ui.TerminalUI;
import com.todoapp.framework.ui.TodoListPresenter;
import com.todoapp.usecase.AddTodoItemUseCase;
import com.todoapp.usecase.ListTodoItemsUseCase;
import com.todoapp.usecase.RemoveTodoItemUseCase;
import com.todoapp.usecase.ToggleTodoItemUseCase;

/**
 * Main application class that wires together all components.
 * This is the entry point that demonstrates Dependency Injection and Clean Architecture.
 */
public class Main {
    private final TodoList todoList;
    private final StorageGateway storageGateway;
    private final TodoListPresenter presenter;
    private final AddTodoItemUseCase addUseCase;
    private final RemoveTodoItemUseCase removeUseCase;
    private final ToggleTodoItemUseCase toggleUseCase;
    private final ListTodoItemsUseCase listUseCase;

    public Main() {
        // Initialize infrastructure adapters
        this.storageGateway = new FileStorage();
        this.presenter = new TerminalUI();

        // Initialize domain objects
        this.todoList = new TodoList(storageGateway.load());

        // Initialize use cases (application layer)
        this.addUseCase = new AddTodoItemUseCase(todoList, storageGateway, presenter);
        this.removeUseCase = new RemoveTodoItemUseCase(todoList, storageGateway, presenter);
        this.toggleUseCase = new ToggleTodoItemUseCase(todoList, storageGateway, presenter);
        this.listUseCase = new ListTodoItemsUseCase(todoList, presenter);
    }

    /**
     * Main entry point of the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Todo List Application Starting...");
        Main app = new Main();
        app.run();
    }

    /**
     * Runs the main application loop.
     */
    public void run() {
        presenter.showSuccess("Welcome to Todo List Application!");
        presenter.showSuccess("Type 'help' for available commands.");

        boolean running = true;
        while (running) {
            try {
                String input = presenter.promptForInput("todo");
                if (input == null || input.isEmpty()) {
                    continue;
                }

                String[] parts = input.split("\\s+", 2);
                String command = parts[0].toLowerCase();
                String argument = parts.length > 1 ? parts[1] : "";

                switch (command) {
                    case "add":
                        if (argument.isEmpty()) {
                            presenter.showError("Please provide a description for the todo item.");
                        } else {
                            addUseCase.execute(argument);
                        }
                        break;
                    case "list":
                        listUseCase.execute(argument);
                        break;
                    case "done":
                        if (argument.isEmpty()) {
                            presenter.showError("Please provide an item ID to mark as completed.");
                        } else {
                            toggleUseCase.execute(argument);
                        }
                        break;
                    case "undone":
                        if (argument.isEmpty()) {
                            presenter.showError("Please provide an item ID to mark as incomplete.");
                        } else {
                            // For simplicity, we'll use the same toggle function
                            // In a more complex app, we might have separate complete/incomplete use cases
                            toggleUseCase.execute(argument);
                        }
                        break;
                    case "remove":
                    case "del":
                        if (argument.isEmpty()) {
                            presenter.showError("Please provide an item ID to remove.");
                        } else {
                            removeUseCase.execute(argument);
                        }
                        break;
                    case "help":
                        showHelp();
                        break;
                    case "exit":
                    case "quit":
                        running = false;
                        presenter.showSuccess("Goodbye!");
                        break;
                    default:
                        presenter.showError("Unknown command: '" + command + "'. Type 'help' for available commands.");
                }
            } catch (Exception e) {
                presenter.showError("An unexpected error occurred: " + e.getMessage());
                // In a production app, we might want to log this properly
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays help information about available commands.
     */
    private void showHelp() {
        presenter.showSuccess("Available commands:");
        presenter.showSuccess("  add <description>   - Add a new todo item");
        presenter.showSuccess("  list [filter]       - List todo items (filter: all, active, completed)");
        presenter.showSuccess("  done <id>           - Mark todo item as completed");
        presenter.showSuccess("  undone <id>         - Mark todo item as incomplete");
        presenter.showSuccess("  remove <id>         - Remove todo item");
        presenter.showSuccess("  help                - Show this help message");
        presenter.showSuccess("  exit/quit           - Exit the application");
        presenter.showSuccess("");
        presenter.showSuccess("Examples:");
        presenter.showSuccess("  add \"Buy groceries\"");
        presenter.showSuccess("  list");
        presenter.showSuccess("  list active");
        presenter.showSuccess("  done 1a3");
        presenter.showSuccess("  remove 1a3");
    }
}