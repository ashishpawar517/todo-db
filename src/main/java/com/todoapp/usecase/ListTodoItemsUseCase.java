package com.todoapp.usecase;

import com.todoapp.domain.TodoItem;
import com.todoapp.domain.TodoList;
import com.todoapp.framework.ui.TodoListPresenter;

/**
 * Use case for listing todo items.
 * Follows Single Responsibility Principle - only handles listing todo items.
 */
public class ListTodoItemsUseCase {
    private final TodoList todoList;
    private final TodoListPresenter presenter;

    public ListTodoItemsUseCase(TodoList todoList, TodoListPresenter presenter) {
        this.todoList = todoList;
        this.presenter = presenter;
    }

    /**
     * Executes the use case to list todo items based on filter.
     *
     * @param filter Filter to apply: "all", "active", or "completed"
     */
    public void execute(String filter) {
        try {
            java.util.List<TodoItem> items;
            switch (filter.toLowerCase()) {
                case "active":
                    items = todoList.getActiveItems();
                    break;
                case "completed":
                    items = todoList.getCompletedItems();
                    break;
                case "all":
                default:
                    items = todoList.getAllItems();
                    break;
            }
            presenter.displayItems(items);
        } catch (Exception e) {
            presenter.showError("Failed to list todo items: " + e.getMessage());
        }
    }

    /**
     * Executes the use case to list all todo items (no filter).
     */
    public void execute() {
        execute("all");
    }
}