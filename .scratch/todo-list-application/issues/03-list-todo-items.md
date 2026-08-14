# 03 — End-to-End List Todo Items Functionality

**What to build:** Implement complete functionality to list todo items with filtering options (all, active, completed) via the command line.

**Blocked by:** 01 — Project Setup and Architecture Verification

**Status:** ready-for-agent

- [ ] Enhance TodoList with methods to get active items and completed items
- [ ] Implement ListTodoItemsUseCase that handles filtering and presentation logic
- [ ] Enhance TerminalUI to display items in a readable tabular format with ID, description, status, and creation date
- [ ] Wire up Main application to recognize "list" command with optional filter parameter
- [ ] Verify that listing shows items correctly formatted with visual indicators for completion status
- [ ] Ensure empty state handling shows appropriate message when no items exist
- [ ] Test filtering functionality: list all, list active, list completed
- [ ] Verify that listed items reflect the current persisted state from storage