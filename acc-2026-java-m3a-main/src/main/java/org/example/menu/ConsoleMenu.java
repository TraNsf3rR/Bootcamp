package org.example.menu;

import org.example.model.Animal;
import org.example.shelter.Shelter;

import java.awt.*;
import java.util.Scanner;

public class ConsoleMenu {
    private final Shelter<Animal> shelter;
    private final Scanner scanner =  new Scanner(System.in);
    public ConsoleMenu(Shelter<Animal> shelter) {
        this.shelter = shelter;
    }

    public void start(){
        // TODO:
        // Call correct 'Shelter' methods based on selected option
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "0" -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                case "1" -> addAnimal();
                case "2" -> {
                    System.out.println("List all animals not implemented yet");
                }
                case "3" -> {
                    System.out.println("Find animals by species not implemented yet");
                }
                case "4" -> {
                    System.out.println("List available animals not implemented yet");
                }
                case "5" -> {
                    System.out.println("Mark animal as adopted not implemented yet");
                }
                default -> System.out.println("Invalid option. Try again");
            }
        }
    }

    private void printMenu(){
        System.out.println("""
                1. Add animal
                2. List all animals
                3. Find animals by species
                4. List available animals
                5. Mark animal as adopted
                0. Exit
                """);
    }

    private void addAnimal() {
        System.out.print("Species (Dog, Cat, Bird): ");
        String species = scanner.nextLine().trim();
//          Take animal species
        if (!species.equalsIgnoreCase("Dog")
                && !species.equalsIgnoreCase("Cat")
                && !species.equalsIgnoreCase("Bird")) {
            System.out.println("Invalid species. Choose Dog, Cat, Bird.");
            return;
        }
//          Take animal name
        System.out.print("Animal name: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("Animal name cannot be empty.");
            return;
        }
//          Take input and check if not empty
        System.out.print("Animal age: ");
        String ageInput = scanner.nextLine().trim();
        if (ageInput.isBlank()) {
            System.out.println("Animal age cannot be empty.");
            return;
        }
//        Check for decimal numbers
        int age;
        try {
            age = Integer.parseInt(ageInput);
        } catch (NumberFormatException exception) {
            System.out.println("Animal age must be a whole number.");
            return;
        }
//        Check for negative numbers
        if (age < 0) {
            System.out.println("Animal age cannot be negative.");
            return;
        }
    }


}
