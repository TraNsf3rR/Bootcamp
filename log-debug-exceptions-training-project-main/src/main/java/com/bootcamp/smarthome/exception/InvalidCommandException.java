package com.bootcamp.smarthome.exception;

public class InvalidCommandException extends HomeAutomationException {

    private static final long serialVersionUID = 1L;

    public InvalidCommandException(String message) {
        super(message);
    }
}