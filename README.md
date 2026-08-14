# Todo List Application

A command-line todo list application built with Java that implements clean architecture principles, adheres to SOLID design principles, and provides an old Linux-style terminal interface.

## Features

- **Clean Architecture**: Separated into distinct layers (Entities, Use Cases, Interface Adapters, Frameworks & Drivers)
- **SOLID Principles**: Follows all five SOLID principles for maintainable, extensible code
- **Persistent Storage**: Saves todo items to a human-readable text file (`todo.txt`)
- **Terminal Interface**: Classic Unix-style interface with ANSI colors and clear formatting
- **Core Functionality**: Add, list, toggle completion, and remove todo items
- **Filtering**: View all items, active items, or completed items
- **Comprehensive Tests**: Full test suite covering domain logic, use cases, storage, and UI

## Architecture Layers

### Entities Layer
- `TodoItem`: Represents a single todo task with identity and behavior
- `TodoList`: Manages a collection of todo items

### Use Cases Layer
- `AddTodoItemUseCase`: Add new todo items
- `RemoveTodoItemUseCase`: Remove todo items
- `ToggleTodoItemUseCase`: Toggle completion status
- `ListTodoItemsUseCase`: List items with filtering options

### Interface Adapters Layer
- `StorageGateway`: Interface for persistence operations
- `TodoListPresenter`: Interface for UI presentation

### Frameworks & Drivers Layer
- `FileStorage`: File-based implementation of StorageGateway
- `TerminalUI`: Terminal-based implementation of TodoListPresenter
- `Main`: Application entry point that wires everything together

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Building the Application
```bash
mvn clean compile
```

### Running the Application
```bash
mvn compile exec:java -Dexec.mainClass="com.todoapp.Main"
```

### Running Tests
```bash
mvn test
```

## Usage

Once the application is running, you can use the following commands:

- `add "description"` - Add a new todo item
- `list` - Show all todo items
- `list active` - Show only active (incomplete) items
- `list completed` - Show only completed items
- `done <id>` - Mark a todo item as completed
- `undone <id>` - Mark a completed todo item as active
- `remove <id>` - Remove a todo item from the list
- `help` - Show available commands
- `exit` or `quit` - Exit the application

### Examples
```bash
add "Buy groceries"
add "Walk the dog"
list
done abc
list active
remove def456
```

## Storage Format

Todo items are stored in a human-readable text file (`todo.txt`) with pipe-delimited format:
```
id|description|completed|createdAt|completedAt
```

Example:
```
abc123-ef45-6789|Buy groceries|false|2026-08-07T10:30:00Z|
def456-ab78-9012|Walk the dog|true|2026-08-07T09:15:00Z|2026-08-07T09:45:00Z
```

## Design Principles

### Clean Architecture
The application follows Robert C. Martin's Clean Architecture principles:
- Entities encapsulate enterprise-wide business rules
- Use cases contain application-specific business rules
- Interface adapters convert data between formats
- Frameworks and drivers contain implementation details

### SOLID Principles
- **S**ingle Responsibility: Each class has one reason to change
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subtypes must be substitutable for their base types
- **I**nterface Segregation: Clients shouldn't depend on interfaces they don't use
- **D**ependency Inversion: Depend on abstractions, not concretions

## Testing

The application includes a comprehensive test suite:
- Unit tests for domain entities (`TodoItem`, `TodoList`)
- Unit tests for all use cases with Mockito mocking
- Unit tests for storage layer (`FileStorage`)
- Unit tests for UI layer (`TerminalUI`)
- Integration tests for main application wiring

Run tests with: `mvn test`

## Project Structure
```
todo-list/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/todoapp/
│   │           ├── domain/              # Business entities
│   │           ├── usecase/             # Application use cases
│   │           ├── framework/           # Interface adapters
│   │           │   ├── storage/         # Storage gateway implementations
│   │           │   └── ui/              # UI gateway implementations
│   │           └── Main.java            # Application entry point
│   └── test/
│       └── java/
│           └── com/todoapp/             # Test classes mirroring main structure
├── pom.xml                              # Maven configuration
�└── README.md                            # This file
```

## Future Enhancements

Potential enhancements that would align with the current architecture:
- Add due dates and recurrence patterns
- Implement priority levels or tagging systems
- Create alternative storage implementations (database, JSON)
- Build alternative UIs (web frontend, GUI desktop app)
- Add batch operations or CSV import/export
- Implement undo/redo functionality
- Add data validation and constraints
- Create reporting or analytics features

The clean architecture and SOLID principles ensure that such enhancements can be made with minimal impact on existing code.

---

Built with Java 17 following clean architecture and SOLID principles.