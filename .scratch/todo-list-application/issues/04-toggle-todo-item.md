# 04 — End-to-End Toggle Todo Item Completion

**What to build:** Implement complete functionality to mark a todo item as completed or active again via the command line.

**Blocked by:** 01 — Project Setup and Architecture Verification

**Status:** ready-for-agent

- [ ] Implement toggleCompletion method in TodoItem domain object
- [ ] Implement toggleItem method in TodoList domain object
- [ ] Implement ToggleTodoItemUseCase that coordinates toggling an item's completion status
- [ ] Wire up Main application to recognize "done" command (for completing) and "undone" command (for activating)
- [ ] Verify that toggling an item persists the change to the storage file
- [ ] Ensure proper error handling for non-existent item IDs
- [ ] Test that the full flow works: user types "done <id>" → item marked as completed → confirmation shown → item persists as completed in storage
- [ ] Test that the reverse flow works: user types "undone <id>" → item marked as active → confirmation shown → item persists as active in storage
- [ ] Verify visual feedback shows correct status indicators ([DONE] vs [TODO]) in list views