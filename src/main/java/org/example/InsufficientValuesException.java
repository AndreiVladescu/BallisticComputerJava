package org.example;

/**
 * Exception thrown when there are insufficient values in a list to perform a specific operation.
 */
public class InsufficientValuesException extends Exception {
    /**
     * Constructs a new InsufficientValuesException with a default error message.
     */
    public InsufficientValuesException(){
        System.out.println("Not enough values inside the list.");
    }
}
