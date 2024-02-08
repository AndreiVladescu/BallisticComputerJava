package org.example;

/**
 * Exception thrown when division by zero is attempted.
 */
public class DivisionByZeroException extends Exception {
    /**
     * Constructs a new DivisionByZeroException with a default error message.
     */
    public DivisionByZeroException() {
        System.out.println("Division by zero not achievable.");
    }
}
