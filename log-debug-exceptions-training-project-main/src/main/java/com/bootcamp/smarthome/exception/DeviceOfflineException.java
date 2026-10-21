package com.bootcamp.smarthome.exception;

public class DeviceOfflineException extends HomeAutomationException {

    private static final long serialVersionUID = 1L;

    public DeviceOfflineException(String message) {
        super(message);
    }
}