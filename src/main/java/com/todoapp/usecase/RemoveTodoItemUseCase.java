package com.todoapp.usecase;

import com.todoapp.domain.TodoItem;
import com.todoapp.domain.TodoList;
import com.todoapp.framework.storage.StorageGateway;
import com.todoapp.framework.ui.TodoListPresenter;

/**
 * Use case for removing a todo item.
 * Follows Single Responsibility Principle - only handles removing todo items.
 */
public class RemoveTodoItemUseCase {
    private final TodoList todoList;
    private final StorageGateway storageGateway;
    private final TodoListPresenter presenter;

    public RemoveTodoItemUseCase(TodoList todoList, StorageGateway storageGateway, TodoListPresenter presenter) {
        this.todoList = todoList;
        this.storageGateway = storageGateway;
        this.presenter = presenter;
    }

    /**
     * Executes the use case to remove a todo item by ID.
     *
     * @param id ID of the todo item to remove
     */
    public void execute(String id) {
        if (id == null || id.trim().isEmpty()) {
            presenter.showError("Please provide an item ID to remove.");
            return;
        }

        try {
            TodoItem item = todoList.getItem(id);
            if (item == null) {
                presenter.showError("Todo item with ID '" + id + "' not found.");
                return;
            }

            boolean removed = todoList.removeItem(id);
            if (removed) {
                storageGateway.save(todoList.getAllItems());
                presenter.showSuccess("Removed: " + item.getDescription());
            } else {
                presenter.showError("Failed to remove todo item with ID '" + id + "'.");
            }
        } catch (Exception e) {
            presenter.showError("Failed to remove todo item: " + e.getMessage());
        }
    }
}