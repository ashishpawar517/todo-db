## Problem Statement

The todo list application currently uses a simple file-based storage system (FileStorage) that loads all items into memory and provides no querying capabilities. Users must load the entire dataset and filter programmatically in the application code. This approach doesn't scale well for larger datasets and doesn't leverage the power of declarative querying.

Users want to be able to query their todo items using SQL-like syntax directly at the storage layer, enabling efficient filtering, sorting, and data manipulation without loading the entire dataset into application memory.

## Solution

Implement a QueryableStorage class that replaces the existing FileStorage implementation while maintaining the StorageGateway interface. This new storage layer will:

1. Maintain the same file-based persistence format (pipe-delimited todo.txt)
2. Provide SQL-like querying capabilities through an executeQuery method
3. Support basic CRUD operations with WHERE clauses for filtering
4. Keep the same clean architecture principles and SOLID design
5. Be a drop-in replacement for FileStorage

## User Stories

1. As a developer, I want to replace FileStorage with QueryableStorage so that I can leverage SQL-like querying capabilities without changing the application architecture.

2. As a user of the todo list application, I want to be able to filter todo items by completion status using WHERE clauses so that I can quickly find active or completed items.

3. As a user of the todo list application, I want to be able to search for todo items by description using LIKE patterns so that I can find specific tasks quickly.

4. As a user of the todo list application, I want to be able to query items by date ranges so that I can find items created or completed within specific time periods.

5. As a developer, I want the QueryableStorage to maintain the same StorageGateway interface so that existing use cases continue to work without modification.

6. As a developer, I want QueryableStorage to persist data in the same human-readable format so that I can inspect and manually edit the storage file if needed.

7. As a developer, I want QueryableStorage to handle malformed data gracefully so that the application doesn't crash on corrupted storage files.

8. As a developer, I want QueryableStorage to load items into an internal query engine so that repeated queries don't need to re-parse the storage file each time.

9. As a developer, I want to be able to add, update, and delete items through QueryableStorage so that all CRUD operations are supported.

10. As a developer, I want QueryableStorage to be thread-safe so that multiple operations can occur concurrently without data corruption.

## Implementation Decisions

### Modules to Build/Modify
- **QueryableStorage**: New class implementing StorageGateway with SQL-like querying
- **QueryEngine**: New class responsible for parsing and executing SQL-like queries
- **Main**: Modified to optionally use QueryableStorage instead of FileStorage (via configuration or command-line flag)

### Interfaces Modified
- **StorageGateway**: No changes needed - QueryableStorage implements this existing interface
- **Main constructor**: Modified to accept a StorageGateway implementation (already supports dependency injection)

### Technical Clarifications
- The query engine will initially support: SELECT * FROM todos WHERE [conditions]
- Conditions support: field comparisons (=, !=, <, >, <=, >=), logical operators (AND, OR), NULL checks
- Field names map to TodoItem properties: id, description, completed, createdAt, completedAt
- String literals must be enclosed in single quotes
- Dates should be in ISO 8601 format for proper parsing
- The storage file format remains unchanged: id|description|completed|createdAt|completedAt

### Architectural Decisions
- **Dependency Injection**: Main already uses DI for StorageGateway, so swapping implementations requires no architectural changes
- **Lazy Loading**: QueryableStorage loads items from file on each load() call and feeds them to the QueryEngine
- **Separation of Concerns**: QueryEngine focuses solely on query parsing/execution; QueryableStorage handles persistence and application integration
- **Fail Fast**: Invalid queries or malformed data are handled gracefully with appropriate error messages or fallback behavior

### API Contracts
- **QueryableStorage.executeQuery(String query)**: Returns List<TodoItem> matching the query
- Supported query: SELECT * FROM todos WHERE [condition] [AND/OR [condition]]*
- Supported conditions: field OP value, field IS NULL, field IS NOT NULL
- Supported operators: =, !=, <, >, <=, >=
- Supported values: strings ('value'), booleans (true/false), dates (ISO 8601)

### Schema Changes
- None - the storage format remains identical to maintain backward compatibility
- The same todo.txt file format is used: id|description|completed|createdAt|completedAt

## Testing Decisions

### What Makes a Good Test
- Tests should verify external behavior, not internal implementation details
- Tests should cover both positive and negative cases
- Tests should verify that the StorageGateway contract is maintained
- Tests should verify query functionality against known datasets
- Tests should verify data persistence and recovery

### Modules to Test
- **QueryableStorage**: Test all StorageGateway methods plus query functionality
- **QueryEngine**: Test query parsing and execution logic
- **Integration**: Test that QueryableStorage works correctly with existing use cases

### Prior Art
- Existing FileStorageTest.java provides excellent patterns for testing storage implementations
- Use case tests demonstrate how to test with mocked StorageGateway implementations
- The test suite already uses JUnit 5 and Assertions, which should be continued

## Out of Scope
- Full SQL implementation (JOINs, subqueries, transactions, etc.)
- Database server or network capabilities
- Advanced query features like ORDER BY, LIMIT, OFFSET, GROUP BY
- Indexing optimizations for large datasets (though the design allows for future extension)
- Concurrency control beyond basic thread safety
- Data validation constraints beyond null checking

## Further Notes
This implementation provides a foundation that can be extended in the future:
- Adding support for ORDER BY, LIMIT, OFFSET clauses
- Implementing proper indexing for faster queries
- Adding support for UPDATE and DELETE queries through the query engine
- Supporting parameterized queries to prevent injection-like issues
- Adding logging and diagnostics for query performance

The design maintains the clean architecture principles of the original codebase while enhancing functionality in a backward-compatible way.