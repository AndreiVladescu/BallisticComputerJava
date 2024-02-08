package org.example;

/**
 * Exception thrown when a null list of values is encountered.
 */
public class NullListException extends Exception {
    /**
     * Constructs a new NullListException with a default error message.
     */
    public NullListException(){
        System.out.println("Null list of values");
    }
}
