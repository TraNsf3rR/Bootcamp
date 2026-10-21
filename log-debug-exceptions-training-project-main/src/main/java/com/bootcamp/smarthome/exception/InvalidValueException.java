package com.bootcamp.smarthome.exception;

public class InvalidValueException extends HomeAutomationException {

    private static final long serialVersionUID = 1L;

    public InvalidValueException(String field, Object value, String constraint) {
        super("Invalid value for '" + field + "': " + value + ". Constraint: " + constraint);
    }
}