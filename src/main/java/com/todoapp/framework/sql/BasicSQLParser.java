package com.todoapp.framework.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic implementation of SQLParser that handles simple SELECT queries with WHERE clauses.
 * This is a starting point that can be extended to support more SQL features.
 */
public class BasicSQLParser implements SQLParser {

    @Override
    public Statement parse(String sql) throws SQLParsingException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new SQLParsingException("SQL query cannot be empty");
        }

        String trimmedSql = sql.trim();
        String upperSql = trimmedSql.toUpperCase();

        // Handle SELECT * FROM tablename [WHERE condition]
        if (upperSql.startsWith("SELECT * FROM ")) {
            // Extract table name and WHERE clause
            String remaining = trimmedSql.substring("SELECT * FROM ".length());

            int whereIndex = upperSql.indexOf(" WHERE ");
            String tableName;
            String whereClauseSql;

            if (whereIndex == -1) {
                // No WHERE clause
                tableName = remaining.trim();
                whereClauseSql = null;
            } else {
                // Has WHERE clause
                tableName = remaining.substring(0, whereIndex).trim();
                whereClauseSql = trimmedSql.substring(whereIndex + 7); // +7 for " WHERE "
            }

            // Validate table name (for now, we only support "todos")
            if (!tableName.equalsIgnoreCase("todos")) {
                throw new SQLParsingException("Unsupported table: " + tableName + ". Only 'todos' is supported.");
            }

            // Parse WHERE clause if present
            Expression whereClause = null;
            if (whereClauseSql != null && !whereClauseSql.trim().isEmpty()) {
                whereClause = parseWhereClause(whereClauseSql);
            }

            return new SelectStatement(tableName, whereClause);
        }

        // For now, we only support SELECT queries. Other statement types will be added later.
        throw new SQLParsingException("Only SELECT statements are currently supported. Received: " + sql);
    }

    /**
     * Parses a WHERE clause expression.
     * This is a simplified parser that handles the basics.
     * A full implementation would use proper parsing techniques (recursive descent, etc.).
     */
    private Expression parseWhereClause(String whereClause) throws SQLParsingException {
        // Remove extra whitespace
        whereClause = whereClause.trim();

        // Handle IS NULL and IS NOT NULL first (they don't have comparison operators)
        String upperWhere = whereClause.toUpperCase();
        if (upperWhere.endsWith(" IS NOT NULL")) {
            String fieldName = whereClause.substring(0, whereClause.length() - 12).trim();
            Expression fieldExpr = new FieldReferenceExpression(fieldName);
            return new IsNullExpression(fieldExpr, false); // false = IS NOT NULL
        } else if (upperWhere.endsWith(" IS NULL")) {
            String fieldName = whereClause.substring(0, whereClause.length() - 8).trim();
            Expression fieldExpr = new FieldReferenceExpression(fieldName);
            return new IsNullExpression(fieldExpr, true); // true = IS NULL
        }

        // Handle AND and OR (split on these operators)
        // This is a very simplified approach - a real parser would handle precedence properly
        if (upperWhere.contains(" AND ")) {
            return parseLogicalExpression(whereClause, LogicalOperator.AND);
        } else if (upperWhere.contains(" OR ")) {
            return parseLogicalExpression(whereClause, LogicalOperator.OR);
        }

        // Handle comparison operations
        return parseComparisonExpression(whereClause);
    }

    private Expression parseLogicalExpression(String expression, LogicalOperator operator) throws SQLParsingException {
        // Split on the operator, preserving case in operands
        String[] parts;
        String regex = "(?i) " + operator + " "; // Case-insensitive split on " AND " or " OR "
        parts = expression.split(regex);

        if (parts.length != 2) {
            throw new SQLParsingException("Invalid logical expression: " + expression);
        }

        Expression left = parseWhereClause(parts[0].trim());
        Expression right = parseWhereClause(parts[1].trim());

        return new LogicalExpression(left, operator, right);
    }

    private Expression parseComparisonExpression(String expression) throws SQLParsingException {
        // Look for comparison operators in order of length to avoid partial matches
        String[] operators = {"!=", ">=", "<=", "=", ">", "<"};
        String upperExpr = expression.toUpperCase();

        for (String op : operators) {
            int opIndex = upperExpr.indexOf(op);
            if (opIndex >= 0) {
                // Found the operator, split the expression
                String leftStr = expression.substring(0, opIndex).trim();
                String rightStr = expression.substring(opIndex + op.length()).trim();

                Expression leftExpr = parseTerm(leftStr);
                Expression rightExpr = parseTerm(rightStr);
                ComparisonOperator comparisonOp = ComparisonOperator.fromSymbol(op);

                return new ComparisonExpression(leftExpr, comparisonOp, rightExpr);
            }
        }

        throw new SQLParsingException("Unable to parse comparison expression: " + expression);
    }

    private Expression parseTerm(String term) throws SQLParsingException {
        term = term.trim();
        if (term.isEmpty()) {
            throw new SQLParsingException("Empty term in expression");
        }

        // Handle string literals (single quotes)
        if (term.startsWith("'") && term.endsWith("'") && term.length() > 2) {
            String value = term.substring(1, term.length() - 1);
            return new LiteralExpression(value);
        }

        // Handle boolean literals
        if (term.equalsIgnoreCase("true") || term.equalsIgnoreCase("false")) {
            return new LiteralExpression(Boolean.parseBoolean(term));
        }

        // Handle numeric literals (integers for now)
        if (term.matches("-?\\d+")) {
            return new LiteralExpression(Integer.parseInt(term));
        }

        // Handle field references
        return new FieldReferenceExpression(term);
    }
}