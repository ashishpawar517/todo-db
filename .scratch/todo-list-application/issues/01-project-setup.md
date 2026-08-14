# 01 — Project Setup and Architecture Verification

**What to build:** Set up the basic project structure with Maven, create a simple Main class that demonstrates the application can compile and run, and verify the foundational architecture pieces are in place.

**Blocked by:** None — can start immediately.

**Status:** ready-for-agent

- [ ] Create Maven pom.xml with Java 17 configuration
- [ ] Set up standard Maven directory structure (src/main/java, src/test/java)
- [ ] Create Main.java in com.todoapp package that prints "Todo List Application Starting..."
- [ ] Verify the project compiles with `mvn compile`
- [ ] Verify the project runs with `mvn exec:java` (or equivalent)
- [ ] Create basic package structure:
  - com.todoapp (main)
  - com.todoapp.domain
  - com.todoapp.usecase
  - com.todoapp.framework.storage
  - com.todoapp.framework.ui
- [ ] Create StorageGateway interface in framework.storage package
- [ ] Create TodoListPresenter interface in framework.ui package