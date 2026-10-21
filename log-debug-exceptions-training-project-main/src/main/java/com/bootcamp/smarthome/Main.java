package com.bootcamp.smarthome;

import com.bootcamp.smarthome.controller.HomeController;
import com.bootcamp.smarthome.device.Device;
import com.bootcamp.smarthome.device.SmartLight;
import com.bootcamp.smarthome.device.SmartLock;
import com.bootcamp.smarthome.device.SmartThermostat;
import com.bootcamp.smarthome.exception.HomeAutomationException;
import com.bootcamp.smarthome.exception.InvalidCommandException;
import com.bootcamp.smarthome.exception.InvalidValueException;

/**
 * Entry point for the Smart Home Controller demo.
 *
 * Registers several smart devices, then runs a series of scenarios that
 * exercise normal operation as well as edge cases and error conditions.
 */
public class Main {

    public static void main(String[] args) {

        HomeController controller = new HomeController();

        System.out.println("=== Setting up devices ===");
        controller.addDevice(new SmartLight("LIGHT_01", "Living Room Light", true));
        controller.addDevice(new SmartLight("LIGHT_02", "Bedroom Light", true));
        controller.addDevice(new SmartThermostat("THERMO_01", "Main Thermostat", true));
        controller.addDevice(new SmartLock("LOCK_01", "Front Door Lock", true, "4321"));
        controller.addDevice(new SmartLight("LIGHT_03", "Kitchen Light", false)); // offline
        controller.addDevice(new SmartThermostat("THERMO_02", "Bedroom Thermostat", true));
        controller.addDevice(new SmartLight("LIGHT_04", "Hallway Light", true));
        controller.addDevice(new SmartLock("LOCK_02", "Back Door Lock", true, "9999"));
        // Array is now FULL (8/8 devices)

        controller.printAllDevices();

        System.out.println("\n=== Scenario 1: Normal device commands ===");
        sendCommandSafely(controller, "LIGHT_01 TURN_ON");
        sendCommandSafely(controller, "LIGHT_02 TURN_ON");
        sendCommandSafely(controller, "THERMO_01 SET_TEMP 22.5");

        System.out.println("\n=== Scenario 2: Set brightness ===");
        sendCommandSafely(controller, "LIGHT_01 SET_BRIGHTNESS 80");

        System.out.println("\n=== Scenario 3: Invalid temperature ===");
        // This test calls setTemperature() directly to isolate temperature validation.
        Device found = controller.findDevice("THERMO_01");
        SmartThermostat mainThermostat = (SmartThermostat) found;
        try {
            mainThermostat.setTemperature(99.0);
        } catch (InvalidValueException exception) {
            exception.printStackTrace(System.err);
        }

        System.out.println("\n=== Scenario 4: Offline device ===");
        // LIGHT_03 is offline — command should be skipped with a warning.
        sendCommandSafely(controller, "LIGHT_03 TURN_ON");

        System.out.println("\n=== Scenario 5: Unlock with correct PIN ===");
        // Direct call to validatePin() to demonstrate intended correct behaviour.
        Device foundLock = controller.findDevice("LOCK_01");
        SmartLock frontDoor = (SmartLock) foundLock;
        try {
            frontDoor.validatePin("4321");
        } catch (InvalidCommandException exception) {
            exception.printStackTrace(System.err);
        }

        System.out.println("\n=== Scenario 6: Unlock with null PIN ===");
        sendCommandSafely(controller, "LOCK_02 UNLOCK");

        System.out.println("\n=== Scenario 7: Find non-existent device ===");
        sendCommandSafely(controller, "SENSOR_99 TURN_ON");

        System.out.println("\n=== All scenarios complete ===");
        controller.printAllDevices();
    }

    private static void sendCommandSafely(HomeController controller, String fullCommand) {
        try {
            controller.sendCommand(fullCommand);
        } catch (HomeAutomationException exception) {
            exception.printStackTrace(System.err);
        }
    }
}