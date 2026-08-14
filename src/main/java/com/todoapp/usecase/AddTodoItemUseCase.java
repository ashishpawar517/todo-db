package com.todoapp.usecase;

import com.todoapp.domain.TodoItem;
import com.todoapp.domain.TodoList;
import com.todoapp.framework.storage.StorageGateway;
import com.todoapp.framework.ui.TodoListPresenter;

/**
 * Use case for adding a new todo item.
 * Follows Single Responsibility Principle - only handles adding todo items.
 */
public class AddTodoItemUseCase {
    private final TodoList todoList;
    private final StorageGateway storageGateway;
    private final TodoListPresenter presenter;

    public AddTodoItemUseCase(TodoList todoList, StorageGateway storageGateway, TodoListPresenter presenter) {
        this.todoList = todoList;
        this.storageGateway = storageGateway;
        this.presenter = presenter;
    }

    /**
     * Executes the use case to add a new todo item.
     *
     * @param description Description of the todo item to add
     */
    public void execute(String description) {
        if (description == null || description.trim().isEmpty()) {
            presenter.showError("Please provide a description for the todo item.");
            return;
        }

        try {
            TodoItem item = todoList.addItem(description);
            storageGateway.save(todoList.getAllItems());
            presenter.showSuccess("Added: " + item.getDescription());
        } catch (Exception e) {
            presenter.showError("Failed to add todo item: " + e.getMessage());
        }
    }
}