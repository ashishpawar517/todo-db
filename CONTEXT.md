# Todo List Database Context

## Core Concepts

### TodoItem
Represents a single todo task with the following attributes:
- **id**: Unique identifier (String)
- **description**: Task description (String)
- **completed**: Completion status (boolean)
- **createdAt**: Creation timestamp (Instant)
- **completedAt**: Completion timestamp (Instant, nullable)

### Database
A file-based, query-capable storage system that replaces the existing FileStorage implementation while providing SQL-like querying capabilities.

### Query Engine
A SQL-inspired query processor that supports:
- SELECT with WHERE clauses for filtering
- INSERT for adding new items
- UPDATE for modifying existing items
- DELETE for removing items
- Basic comparison operators (=, !=, <, >, <=, >=)
- Logical operators (AND, OR)
- String matching (LIKE)
- NULL checks (IS NULL, IS NOT NULL)

### Storage Format
Pipe-delimited text file with the same format as the existing FileStorage:
`id|description|completed|createdAt|completedAt`

## Key Decisions

### Implementation Language
**Decision**: Java 17
**Reasoning**: Maintain consistency with existing Maven-based Java project, leverage existing build/test infrastructure, and follow project's clean architecture principles.

### SQL Compatibility
**Decision**: Basic SQL subset with extensible design
**Supported initially**:
- SELECT * FROM todos WHERE [conditions]
- INSERT INTO todos (id, description, completed, createdAt, completedAt) VALUES (...)
- UPDATE todos SET completed = true WHERE id = '...'
- DELETE FROM todos WHERE id = '...'
**Extensible to**: JOINs, aggregation, GROUP BY, etc.

### Storage Mechanism
**Decision**: File-based persistent storage
**Reasoning**: Match current architecture, provide zero-configuration deployment, maintain human-readable format, and ensure data persistence across restarts.

### Application Interface
**Decision**: Drop-in replacement for FileStorage implementing StorageGateway
**Reasoning**: Preserve existing clean architecture, maintain SOLID principles (especially Dependency Inversion), and enable easy switching between storage implementations.

### Primary Goal
**Decision**: Learning exercise with practical value
**Reasoning**: Building a custom database provides deep learning about data storage, indexing, query parsing, and execution while delivering a usable enhancement to the todo list application.

### Query Language Design
**Decision**: SQL-inspired but simplified for todo list domain
**Reasoning**: Full SQL implementation is overkill for todo list needs; a simplified query language provides educational value while being implementable and maintainable.