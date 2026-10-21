package com.bootcamp.smarthome.exception;

public class DeviceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DeviceNotFoundException(String message) {
        super(message);
    }
}