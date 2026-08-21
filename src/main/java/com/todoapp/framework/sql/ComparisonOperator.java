package com.todoapp.framework.sql;

/**
 * Enumeration of comparison operators supported in SQL-like expressions.
 */
public enum ComparisonOperator {
    EQUALS("="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_EQUAL("<="),
    GREATER_THAN_EQUAL(">=");

    private final String symbol;

    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the ComparisonOperator for a given symbol.
     * @param symbol the operator symbol (e.g., "=", "!=", "<", etc.)
     * @return the corresponding ComparisonOperator
     * @throws IllegalArgumentException if the symbol is not recognized
     */
    public static ComparisonOperator fromSymbol(String symbol) {
        for (ComparisonOperator op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown comparison operator: " + symbol);
    }
}