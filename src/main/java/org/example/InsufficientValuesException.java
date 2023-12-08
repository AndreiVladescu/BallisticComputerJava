package org.example;

public class InsufficientValuesException extends Exception {
    public InsufficientValuesException(){
        System.out.println("Not enough values inside the list.");
    }
}
