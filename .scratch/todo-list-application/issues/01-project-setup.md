# 01 — Project Setup and Architecture Verification

**What to build:** Set up the basic project structure with Maven, create a simple Main class that demonstrates the application can compile and run, and verify the foundational architecture pieces are in place.

**Blocked by:** None — can start immediately.

**Status:** ready-for-agent

- [x] Create Maven pom.xml with Java 17 configuration
- [x] Set up standard Maven directory structure (src/main/java, src/test/java)
- [x] Create Main.java in com.todoapp package that prints "Todo List Application Starting..."
- [x] Verify the project compiles with `mvn compile`
- [x] Verify the project runs with `mvn exec:java` (or equivalent)
- [x] Create basic package structure:
  - com.todoapp (main)
  - com.todoapp.domain
  - com.todoapp.usecase
  - com.todoapp.framework.storage
  - com.todoapp.framework.ui
- [x] Create StorageGateway interface in framework.storage package
- [x] Create TodoListPresenter interface in framework.ui package