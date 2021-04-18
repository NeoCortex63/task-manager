package com.neocortex.taskmanager.exceptions;

public class EmailAlreadyTakenException extends Exception{

    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
