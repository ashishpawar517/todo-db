package com.todoapp.framework.storage.database;

import com.todoapp.domain.TodoItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple SQL-like query engine for TodoItem objects.
 * Supports basic SELECT queries with WHERE clauses.
 */
public class QueryEngine {
    private List<TodoItem> indexedItems = new ArrayList<>();

    /**
     * Loads items into the query engine for indexing.
     * In a full implementation, this would create indexes for faster querying.
     *
     * @param items List of TodoItem objects to index
     */
    public void loadItems(List<TodoItem> items) {
        this.indexedItems = new ArrayList<>(items);
    }

    /**
     * Executes a SQL-like query on the stored items.
     *
     * Supported query format:
     * SELECT * FROM todos WHERE [conditions]
     *
     * Conditions support:
     * - Field comparisons: id = 'value', completed = true, etc.
     * - Logical operators: AND, OR
     * - Comparison operators: =, !=, <, >, <=, >=
     * - String literals in single quotes
     * - NULL checks: IS NULL, IS NOT NULL
     *
     * @param query SQL-like query string
     * @param items List of TodoItem objects to query against
     * @return List of matching TodoItem objects
     */
    public List<TodoItem> executeQuery(String query, List<TodoItem> items) {
        // For simplicity, we'll use the passed items rather than indexedItems
        // In a more advanced implementation, we would use indexedItems and maintain indexes

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(items);
        }

        String trimmedQuery = query.trim();

        // Basic validation - only support SELECT * FROM todos for now (case insensitive)
        if (!trimmedQuery.toUpperCase().startsWith("SELECT * FROM TODOS")) {
            // If not a recognized query format, return all items
            // In a real implementation, we would parse and execute the query properly
            return new ArrayList<>(items);
        }

        // Extract WHERE clause if present - need to preserve case for string literals
        int whereIndex = trimmedQuery.toUpperCase().indexOf(" WHERE ");
        if (whereIndex == -1) {
            // No WHERE clause, return all items
            return new ArrayList<>(items);
        }

        // Extract the WHERE clause preserving original case for string literals
        String whereClause = trimmedQuery.substring(whereIndex + 7); // Length of " WHERE "

        // Parse and execute the WHERE clause
        return filterItems(items, whereClause);
    }

    /**
     * Filters items based on a WHERE clause.
     *
     * @param items List of TodoItem objects to filter
     * @param whereClause WHERE clause expression (uppercase)
     * @return List of matching TodoItem objects
     */
    private List<TodoItem> filterItems(List<TodoItem> items, String whereClause) {
        List<TodoItem> result = new ArrayList<>();

        for (TodoItem item : items) {
            if (evaluateWhereClause(item, whereClause)) {
                result.add(item);
            }
        }

        return result;
    }

    /**
     * Evaluates whether a single item matches the WHERE clause.
     *
     * @param item TodoItem to evaluate
     * @param whereClause WHERE clause expression (preserving case for literals)
     * @return true if the item matches the clause, false otherwise
     */
    private boolean evaluateWhereClause(TodoItem item, String whereClause) {
        // Simple implementation for demonstration
        // A real implementation would parse the expression properly

        // Handle AND and OR by splitting (case insensitive for SQL keywords)
        String upperWhereClause = whereClause.toUpperCase();
        if (upperWhereClause.contains(" AND ")) {
            // Split on " AND " preserving original case in parts
            String[] conditions = whereClause.split("(?i) AND ");
            for (String condition : conditions) {
                if (!evaluateWhereClause(item, trimSurroundingParentheses(condition.trim()))) {
                    return false;
                }
            }
            return true;
        } else if (upperWhereClause.contains(" OR ")) {
            // Split on " OR " preserving original case in parts
            String[] conditions = whereClause.split("(?i) OR ");
            for (String condition : conditions) {
                if (evaluateWhereClause(item, trimSurroundingParentheses(condition.trim()))) {
                    return true;
                }
            }
            return false;
        } else {
            // Single condition
            return evaluateCondition(item, trimSurroundingParentheses(whereClause.trim()));
        }
    }

    /**
     * Trims surrounding parentheses from a string.
     * Handles multiple layers of parentheses.
     *
     * @param s String to trim
     * @return String with surrounding parentheses removed
     */
    private String trimSurroundingParentheses(String s) {
        String trimmed = s.trim();
        while (trimmed.startsWith("(") && trimmed.endsWith(")") && trimmed.length() >= 2) {
            // Check if we have matching parentheses by counting
            int openCount = 0;
            int closeCount = 0;
            boolean balanced = true;

            for (int i = 0; i < trimmed.length(); i++) {
                char c = trimmed.charAt(i);
                if (c == '(') {
                    openCount++;
                } else if (c == ')') {
                    closeCount++;
                    if (closeCount > openCount) {
                        balanced = false;
                        break;
                    }
                }
            }

            if (balanced && openCount == closeCount) {
                // Remove the outermost parentheses
                trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
            } else {
                break;
            }
        }
        return trimmed;
    }

    /**
     * Evaluates a single condition against an item.
     *
     * @param item TodoItem to evaluate
     * @param condition Condition expression (preserving case for literals)
     * @return true if the item matches the condition, false otherwise
     */
    private boolean evaluateCondition(TodoItem item, String condition) {
        // Handle NULL checks (case insensitive for SQL keywords)
        String upperCondition = condition.toUpperCase();
        if (upperCondition.endsWith(" IS NULL")) {
            String field = condition.substring(0, condition.length() - 8).trim();
            return evaluateField(item, field, null, "=");
        } else if (upperCondition.endsWith(" IS NOT NULL")) {
            String field = condition.substring(0, condition.length() - 12).trim();
            return !evaluateField(item, field, null, "=");
        }

        // Handle comparison operators
        String[] operators = {"!=", ">=", "<=", "=", ">", "<"};
        for (String op : operators) {
            // Look for operator with spaces around it (case insensitive for field and value, but preserve spaces)
            int opIndex = upperCondition.indexOf(" " + op + " ");
            if (opIndex >= 0) {
                String field = condition.substring(0, opIndex).trim();
                String valueStr = condition.substring(opIndex + op.length() + 2).trim(); // +2 for the spaces

                // Remove quotes from string literals
                if (valueStr.startsWith("'") && valueStr.endsWith("'") && valueStr.length() > 2) {
                    valueStr = valueStr.substring(1, valueStr.length() - 1);
                }

                Object value = convertValue(valueStr);
                return evaluateField(item, field, value, op);
            }
        }

        // If we can't parse the condition, assume it doesn't match
        // In a real implementation, we would throw an exception or log an error
        return false;
    }

    /**
     * Evaluates a field condition against an item.
     *
     * @param item TodoItem to evaluate
     * @param field Field name (ID, DESCRIPTION, COMPLETED, CREATEDAT, COMPLETEDAT)
     * @param value Value to compare against
     * @param operator Comparison operator (=, !=, <, >, <=, >=)
     * @return true if the item matches the condition, false otherwise
     */
    private boolean evaluateField(TodoItem item, String field, Object value, String operator) {
        Object fieldValue = null;

        switch (field.toUpperCase()) {
            case "ID":
                fieldValue = item.getId();
                break;
            case "DESCRIPTION":
                fieldValue = item.getDescription();
                break;
            case "COMPLETED":
                fieldValue = item.isCompleted();
                break;
            case "CREATEDAT":
                fieldValue = item.getCreatedAt();
                break;
            case "COMPLETEDAT":
                fieldValue = item.getCompletedAt();
                break;
            default:
                // Unknown field
                return false;
        }

        // Handle null values
        if (value == null) {
            // value is null, check for IS NULL or IS NOT NULL semantics
            if (operator.equals("=")) {
                return fieldValue == null;
            } else { // "!=" or "<>"
                return fieldValue != null;
            }
        }

        // Perform comparison based on field type
        if (fieldValue instanceof String) {
            return compareStrings((String) fieldValue, (String) value, operator);
        } else if (fieldValue instanceof Boolean) {
            return compareBooleans((Boolean) fieldValue, (Boolean) value, operator);
        } else if (fieldValue instanceof Instant) {
            return compareInstants((Instant) fieldValue, (Instant) value, operator);
        }

        // Fallback to string comparison
        return compareStrings(fieldValue.toString(), value.toString(), operator);
    }

    /**
     * Compares two strings based on the operator.
     */
    private boolean compareStrings(String a, String b, String operator) {
        switch (operator) {
            case "=":
                return a.equals(b);
            case "!=":
            case "<>":
                return !a.equals(b);
            case "<":
                return a.compareTo(b) < 0;
            case ">":
                return a.compareTo(b) > 0;
            case "<=":
                return a.compareTo(b) <= 0;
            case ">=":
                return a.compareTo(b) >= 0;
            default:
                return false;
        }
    }

    /**
     * Compares two booleans based on the operator.
     */
    private boolean compareBooleans(Boolean a, Boolean b, String operator) {
        switch (operator) {
            case "=":
                return a.equals(b);
            case "!=":
            case "<>":
                return !a.equals(b);
            default:
                return false;
        }
    }

    /**
     * Compares two Instants based on the operator.
     */
    private boolean compareInstants(Instant a, Instant b, String operator) {
        switch (operator) {
            case "=":
                return a.equals(b);
            case "!=":
            case "<>":
                return !a.equals(b);
            case "<":
                return a.isBefore(b);
            case ">":
                return a.isAfter(b);
            case "<=":
                return !a.isAfter(b); // a <= b means a is not after b
            case ">=":
                return !a.isBefore(b); // a >= b means a is not before b
            default:
                return false;
        }
    }

    /**
     * Converts a string value to an appropriate Java object.
     */
    private Object convertValue(String valueStr) {
        if (valueStr == null || valueStr.isEmpty()) {
            return null;
        }

        // Boolean values
        if (valueStr.equalsIgnoreCase("TRUE") || valueStr.equalsIgnoreCase("FALSE")) {
            return Boolean.valueOf(valueStr);
        }

        // Instant values (simplified - in reality we'd parse dates properly)
        if (valueStr.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z?")) {
            try {
                return Instant.parse(valueStr);
            } catch (Exception e) {
                // If parsing fails, treat as string
                return valueStr;
            }
        }

        // Default to string
        return valueStr;
    }
}