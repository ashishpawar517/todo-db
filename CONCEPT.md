# Todo List Application - Domain Concepts

## Core Entities

### TodoItem
Represents a single todo task with the following attributes:
- id: Unique identifier (UUID or incremental number)
- description: Text description of the task
- completed: Boolean indicating completion status
- createdAt: Timestamp when the item was created
- completedAt: Timestamp when the item was completed (null if not completed)

### TodoList
A collection of TodoItem objects that provides:
- Adding new items
- Removing items by ID
- Marking items as completed/incomplete
- Listing all items
- Filtering items by completion status
- Persistence to/from text file storage

## Use Cases

### AddTodoItem
- Input: Description text
- Output: New TodoItem with generated ID
- Side effect: Persists updated list to storage

### RemoveTodoItem
- Input: Item ID
- Output: Success/failure indication
- Side effect: Persists updated list to storage

### ToggleTodoItem
- Input: Item ID
- Output: Updated TodoItem
- Side effect: Persists updated list to storage

### ListTodoItems
- Input: Optional filter (all, active, completed)
- Output: List of TodoItem objects matching filter

### PersistTodoList
- Input: TodoList object
- Output: Success/failure
- Side effect: Writes list to storage file

### LoadTodoList
- Input: None
- Output: TodoList object (empty list if no storage file)
- Side effect: Reads list from storage file

## Interfaces

### StorageGateway
Defines contract for persistence operations:
- save(List<TodoItem>): void
- load(): List<TodoItem>

### TodoListPresenter
Defines contract for UI presentation:
- displayItems(List<TodoItem>): void
- showError(String message): void
- showSuccess(String message): void
- promptForInput(String prompt): String

## Architectural Layers (Clean Architecture)

### Entities Layer
- TodoItem (business rules)
- TodoList (business rules)

### Use Cases Layer
- AddTodoItemUseCase
- RemoveTodoItemUseCase
- ToggleTodoItemUseCase
- ListTodoItemsUseCase

### Interface Adapters Layer
- Controllers (handle UI input)
- Presenters (format output for UI)
- StorageGateway interface

### Frameworks & Drivers Layer
- TerminalUI (implementation of TodoListPresenter)
- FileStorage (implementation of StorageGateway)
- Main application wireup

## SOLID Principles Application

### S - Single Responsibility
- TodoItem: Manages todo item state
- TodoList: Manages collection of todo items
- FileStorage: Handles file I/O operations
- TerminalUI: Handles terminal interaction
- Each use case: Handles one specific user action

### O - Open/Closed
- Use cases are open for extension (new use cases) but closed for modification
- StorageGateway allows adding new storage types without changing use cases
- TodoListPresenter allows adding new UI types without changing use cases

### L - Liskov Substitution
- Any StorageGateway implementation can replace FileStorage
- Any TodoListPresenter implementation can replace TerminalUI

### I - Interface Segregation
- StorageGateway has only storage-related methods
- TodoListPresenter has only presentation-related methods
- Clients depend only on methods they use

### D - Dependency Inversion
- High-level modules (use cases) depend on abstractions (StorageGateway, TodoListPresenter)
- Low-level modules (FileStorage, TerminalUI) depend on abstractions
- Abstractions do not depend on details; details depend on abstractions

## Text File Storage Format

Each line represents a todo item in CSV-like format:
`id|description|completed|createdAt|completedAt`

Example:
`1|Buy groceries|false|2026-08-07T10:30:00Z|`
`2|Walk the dog|true|2026-08-07T09:15:00Z|2026-08-07T09:45:00Z`

Empty completedAt field indicates incomplete item.

## Terminal Interface (Old Linux Style)

Inspired by classic Unix utilities:
- Simple text-based interface
- Minimal dependencies (no external libraries needed)
- Clear prompts and feedback
- Keyboard navigation
- Common command-line patterns:
  - `todo add "description"` - Add new item
  - `todo list` - Show all items
  - `todo done <id>` - Mark item as completed
  - `todo remove <id>` - Remove item
  - `todo help` - Show usage