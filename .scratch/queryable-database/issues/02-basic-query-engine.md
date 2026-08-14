# 02 — Basic Query Engine

**What to build:** Implement core query engine functionality that can parse and execute simple WHERE clauses on TodoItem collections, supporting basic comparisons and logical operators.

**Blocked by:** 01 — Basic Storage Replacement

**Status:** ready-for-agent

- [ ] Parse SELECT * FROM todos WHERE [condition] queries
- [ ] Support equality (=) and inequality (!=) operators for all field types
- [ ] Support comparison operators (<, >, <=, >=) for date and string fields
- [ ] Handle string literals in single quotes
- [ ] Support boolean literals (true/false)
- [ ] Support NULL checks (IS NULL, IS NOT NULL)
- [ ] Support AND and OR logical operators
- [ ] Properly map field names to TodoItem properties (id, description, completed, createdAt, completedAt)
- [ ] Return correct subset of items for given WHERE clause
- [ ] Handle empty WHERE clause (return all items)
- [ ] Gracefully handle malformed queries (return all items or empty list as appropriate)