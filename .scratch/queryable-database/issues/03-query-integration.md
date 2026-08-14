# 03 — Query Integration

**What to build:** Integrate the query engine with QueryableStorage to provide executeQuery() method that loads items from storage and applies the query engine to return filtered results.

**Blocked by:** 01 — Basic Storage Replacement, 02 — Basic Query Engine

**Status:** ready-for-agent

- [ ] Implement executeQuery(String query) method in QueryableStorage
- [ ] Load current items from storage file before executing query
- [ ] Delegate query parsing and execution to QueryEngine
- [ ] Return list of TodoItem objects matching the query
- [ ] Ensure executeQuery works with various WHERE clauses
- [ ] Verify that executeQuery doesn't modify stored data
- [ ] Test that executeQuery works after add/update/remove operations
- [ ] Handle edge cases like empty storage or malformed data
- [ ] Ensure proper error handling doesn't crash the application