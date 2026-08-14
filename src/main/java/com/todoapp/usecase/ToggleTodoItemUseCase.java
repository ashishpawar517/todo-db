package com.todoapp.usecase;

import com.todoapp.domain.TodoItem;
import com.todoapp.domain.TodoList;
import com.todoapp.framework.storage.StorageGateway;
import com.todoapp.framework.ui.TodoListPresenter;

/**
 * Use case for toggling the completion status of a todo item.
 * Follows Single Responsibility Principle - only handles toggling todo items.
 */
public class ToggleTodoItemUseCase {
    private final TodoList todoList;
    private final StorageGateway storageGateway;
    private final TodoListPresenter presenter;

    public ToggleTodoItemUseCase(TodoList todoList, StorageGateway storageGateway, TodoListPresenter presenter) {
        this.todoList = todoList;
        this.storageGateway = storageGateway;
        this.presenter = presenter;
    }

    /**
     * Executes the use case to toggle a todo item's completion status by ID.
     *
     * @param id ID of the todo item to toggle
     */
    public void execute(String id) {
        if (id == null || id.trim().isEmpty()) {
            presenter.showError("Please provide an item ID to mark as completed.");
            return;
        }

        try {
            TodoItem item = todoList.getItem(id);
            if (item == null) {
                presenter.showError("Todo item with ID '" + id + "' not found.");
                return;
            }

            boolean toggled = todoList.toggleItem(id);
            if (toggled) {
                storageGateway.save(todoList.getAllItems());
                String status = item.isCompleted() ? "completed" : "activated";
                presenter.showSuccess("Item '" + item.getDescription() + "' " + status + ".");
            } else {
                presenter.showError("Failed to toggle todo item with ID '" + id + "'.");
            }
        } catch (Exception e) {
            presenter.showError("Failed to toggle todo item: " + e.getMessage());
        }
    }
}