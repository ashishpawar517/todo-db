package com.todoapp.framework.sql;

/**
 * Represents a comparison expression in a SQL-like WHERE clause.
 * Supports comparisons like: field = value, field != value, field < value, etc.
 */
public class ComparisonExpression implements Expression {

    private final Expression leftOperand;
    private final ComparisonOperator operator;
    private final Expression rightOperand;

    public ComparisonExpression(Expression leftOperand, ComparisonOperator operator, Expression rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public Expression getRightOperand() {
        return rightOperand;
    }

    @Override
    public Object evaluate(TodoItem item) {
        Object leftValue = leftOperand.evaluate(item);
        Object rightValue = rightOperand.evaluate(item);

        // Handle null values according to SQL semantics
        if (leftValue == null && rightValue == null) {
            switch (operator) {
                case EQUALS:
                    return true;
                case NOT_EQUALS:
                    return false;
                default:
                    // For ordering comparisons, null == null is considered equal, so not <, >, etc.
                    return false;
            }
        } else if (leftValue == null) {
            // left is null, right is not null
            switch (operator) {
                case EQUALS:
                    return false;
                case NOT_EQUALS:
                    return true;
                case LESS_THAN:
                    return true;  // null is considered less than any non-null value
                case LESS_THAN_EQUAL:
                    return true;  // null is considered less than or equal to any non-null value
                case GREATER_THAN:
                    return false; // null is not greater than any non-null value
                case GREATER_THAN_EQUAL:
                    return false; // null is not greater than or equal to any non-null value
                default:
                    return false;
            }
        } else if (rightValue == null) {
            // right is null, left is not null
            switch (operator) {
                case EQUALS:
                    return false;
                case NOT_EQUALS:
                    return true;
                case LESS_THAN:
                    return false; // non-null value is not less than null
                case LESS_THAN_EQUAL:
                    return false; // non-null value is not less than or equal to null
                case GREATER_THAN:
                    return true;  // non-null value is greater than null
                case GREATER_THAN_EQUAL:
                    return true;  // non-null value is greater than or equal to null
                default:
                    return false;
            }
        }

        // Both operands are non-null, perform comparison
        return compareValues(leftValue, rightValue, operator);
    }

    private boolean compareValues(Object left, Object right, ComparisonOperator operator) {
        // If both values are Strings, compare lexicographically
        if (left instanceof String && right instanceof String) {
            return compareStrings((String) left, (String) right, operator);
        }
        // If both values are Booleans, compare equality
        else if (left instanceof Boolean && right instanceof Boolean) {
            return compareBooleans((Boolean) left, (Boolean) right, operator);
        }
        // If both values are Instants, compare temporally
        else if (left instanceof Instant && right instanceof Instant) {
            return compareInstants((Instant) left, (Instant) right, operator);
        }
        // Fallback: convert to strings and compare lexicographically
        else {
            return compareStrings(left.toString(), right.toString(), operator);
        }
    }

    private boolean compareStrings(String a, String b, ComparisonOperator operator) {
        int comparison = a.compareTo(b);
        switch (operator) {
            case EQUALS:
                return comparison == 0;
            case NOT_EQUALS:
                return comparison != 0;
            case LESS_THAN:
                return comparison < 0;
            case GREATER_THAN:
                return comparison > 0;
            case LESS_THAN_EQUAL:
                return comparison <= 0;
            case GREATER_THAN_EQUAL:
                return comparison >= 0;
            default:
                return false;
        }
    }

    private boolean compareBooleans(Boolean a, Boolean b, ComparisonOperator operator) {
        switch (operator) {
            case EQUALS:
                return a.equals(b);
            case NOT_EQUALS:
                return !a.equals(b);
            default:
                // Boolean ordering is not meaningful in SQL, treat as equals/not equals only
                return false;
        }
    }

    private boolean compareInstants(Instant a, Instant b, ComparisonOperator operator) {
        switch (operator) {
            case EQUALS:
                return a.equals(b);
            case NOT_EQUALS:
                return !a.equals(b);
            case LESS_THAN:
                return a.isBefore(b);
            case GREATER_THAN:
                return a.isAfter(b);
            case LESS_THAN_EQUAL:
                return !a.isAfter(b); // a <= b
            case GREATER_THAN_EQUAL:
                return !a.isBefore(b); // a >= b
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "(" + leftOperand + " " + operator.getSymbol() + " " + rightOperand + ")";
    }
}