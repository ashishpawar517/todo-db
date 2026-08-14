# 01 — Basic Storage Replacement

**What to build:** Replace FileStorage with QueryableStorage as a drop-in implementation that passes all existing storage tests while maintaining the same StorageGateway interface.

**Blocked by:** None — can start immediately.

**Status:** ready-for-agent

- [ ] Verify QueryableStorage implements StorageGateway interface correctly
- [ ] Ensure all existing FileStorage tests pass with QueryableStorage substitution
- [ ] Confirm data persistence works identically to FileStorage (same file format)
- [ ] Validate that load() returns empty list when file doesn't exist
- [ ] Confirm save() and load() work round-trip correctly
- [ ] Handle malformed data gracefully (same as FileStorage)
- [ ] Maintain thread safety for basic operations