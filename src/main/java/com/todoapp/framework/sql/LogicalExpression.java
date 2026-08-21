package com.todoapp.framework.sql;

/**
 * Represents a logical expression (AND/OR) in a SQL-like WHERE clause.
 */
public class LogicalExpression implements Expression {

    private final Expression leftOperand;
    private final LogicalOperator operator;
    private final Expression rightOperand;

    public LogicalExpression(Expression leftOperand, LogicalOperator operator, Expression rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public Expression getRightOperand() {
        return rightOperand;
    }

    @Override
    public Object evaluate(TodoItem item) {
        Object leftValue = leftOperand.evaluate(item);
        Object rightValue = rightOperand.evaluate(item);

        // Logical operations require boolean operands
        boolean leftBool = coerceToBoolean(leftValue);
        boolean rightBool = coerceToBoolean(rightValue);

        switch (operator) {
            case AND:
                return leftBool && rightBool;
            case OR:
                return leftBool || rightBool;
            default:
                return false;
        }
    }

    /**
     * Coerces a value to a boolean according to SQL-like semantics.
     * Null values are treated as false.
     */
    private boolean coerceToBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        // For other types, follow SQL-like truthiness: 0/false equivalent is false, everything else is true
        // But for simplicity in this implementation, we'll only accept actual Boolean values
        // In a more complete implementation, we might convert numbers (0=false, non-zero=true)
        // or strings ("false"/"0"=false, else=true)
        return false;
    }

    @Override
    public String toString() {
        return "(" + leftOperand + " " + operator + " " + rightOperand + ")";
    }
}