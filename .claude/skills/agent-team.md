# Agent Team Skill

This skill provides guidance on creating and managing agent teams for complex tasks.

## When to Use
Use this approach when a task benefits from:
- Multiple areas of expertise (research, coding, testing, review)
- Parallel work that can be decomposed
- Need for specialized agents working together

## Core Concepts

### Agent Roles
Different agent types excel at different tasks:
- **researcher-agent**: Information gathering, web search, reading documentation
- **general-purpose**: Implementation, coding, file modifications
- **code-reviewer**: Quality checks, security, convention compliance
- **test-generator-runner**: Test creation, test execution, verification
- **git-agent**: Version control operations, branching, merging

### Communication
Agents communicate via `SendMessage`:
- Use `SendMessage` to pass results, ask questions, or request help
- Agents can send messages to you (the main conversation) or to each other
- Always summarize what you're sending in the `summary` field

## Workflow Patterns

### Sequential Pipeline
For tasks that must happen in order:
1. Researcher gathers requirements
2. General-purpose agent implements solution
3. Test agent creates and runs tests
4. Code reviewer checks implementation
5. Git agent handles version control

### Parallel Investigation
For exploring multiple approaches:
1. Spawn multiple researcher agents with different angles
2. Have them report back findings
3. Synthesize results before proceeding

### Feedback Loop
For iterative improvement:
1. Implementer creates initial version
2. Tester runs tests and reports issues
3. Implementer fixes based on feedback
4. Reviewer verifies fixes
5. Repeat until satisfactory

## Step-by-Step Guide

### 1. Task Decomposition
Break your big task into subtasks that match agent strengths:
- Information gathering → researcher-agent
- Code changes → general-purpose agent
- Testing → test-generator-runner agent
- Review → code-reviewer agent
- Git operations → git-agent agent

### 2. Spawn Agents
Use the Agent tool for each subtask:
```
Agent({
  description: "Research latest Java testing frameworks",
  prompt: "Find information about JUnit 5, Mockito, and AssertJ for Java testing",
  subagent_type: "researcher-agent"
})
```

### 3. Establish Communication
- Agents should report progress via SendMessage
- Use clear summaries so you can track work without reading full transcripts
- Agents can ask you questions if they need clarification or decisions

### 4. Aggregate Results
As agents complete work:
- Collect their reports and outputs
- Synthesize information for next steps
- Make decisions about direction based on findings

### 5. Clean Up
When agents finish:
- Ensure they've completed their assigned work
- Note any follow-up tasks they discovered
- Thank them for their work (optional but good practice)

## Best Practices

### Start Small
Begin with 2-3 agents for your first team to understand coordination before scaling up.

### Clear Instructions
Give each agent a specific, measurable goal:
- Instead of "help with testing" → "create unit tests for the UserService class"
- Instead of "research databases" → "compare PostgreSQL vs MySQL for this todo app"

### Time Management
Consider that agent work takes time. For long-running tasks:
- Use CronCreate to schedule check-ins
- Or have agents send periodic updates via SendMessage

### Error Handling
If an agent encounters a blocker:
- They should SendMessage you explaining the issue
- You can then provide guidance or adjust the task

## Example: Adding a New Feature

Here's how you might structure agent work for adding a new feature:

1. **Researcher Agent**: 
   - Research similar features in the codebase
   - Look up best practices for the feature type
   - Report findings

2. **General-Purpose Agent**:
   - Implement the feature based on research
   - Create necessary files and modify existing ones

3. **Test Agent**:
   - Write unit tests for new code
   - Run existing tests to ensure no regressions
   - Report test results

4. **Code Reviewer Agent**:
   - Review implementation for quality and conventions
   - Suggest improvements
   - Verify test coverage

5. **Git Agent**:
   - Create feature branch
   - Commit changes with descriptive messages
   - Push to remote and create PR (if applicable)

## Limitations
- Agents cannot see each other's tool output directly (only via SendMessage)
- Complex coordination requires clear communication protocols
- Some tasks may still be better handled inline for simple cases

## When Not to Use
Avoid agent teams for:
- Trivial tasks that take seconds to complete
- Tasks requiring real-time collaboration or shared state
- When you need immediate, line-by-line code review as you type

## Next Steps
To use this skill in your workflow:
1. Invoke with `/agent-team` or via Skill tool
2. Follow the decomposition approach for your specific task
3. Spawn appropriate agents with clear goals
4. Coordinate via SendMessage
5. Synthesize results to complete the big task