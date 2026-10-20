package org.example.menu;

import org.example.model.Animal;
import org.example.shelter.Shelter;

import java.awt.*;
import java.util.Scanner;

import org.example.model.AnimalId;
import org.example.model.Bird;
import org.example.model.Cat;
import org.example.model.Dog;

import java.util.Locale;

public class ConsoleMenu {
    private final Shelter<Animal> shelter;
    private final Scanner scanner =  new Scanner(System.in);
    public ConsoleMenu(Shelter<Animal> shelter) {
        this.shelter = shelter;
    }

    public void start(){
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
                    var animals = shelter.getAllAnimals();

                    if (animals.isEmpty()) {
                        System.out.println("No animals in the shelter.");
                    } else {
                        animals.forEach(System.out::println);
                    }
                }
                case "3" -> {
                    System.out.print("Enter species (Dog, Cat, Bird): ");
                    String species = scanner.nextLine().trim();
                    var animals = shelter.findBySpecies(species);

                    if (animals.isEmpty()) {
                        System.out.println("No animals found for species: " + species);
                    } else {
                        animals.forEach(System.out::println);
                    }
                }
                case "4" -> {
                    var availableAnimals = shelter.findAvailableAnimals();

                    if (availableAnimals.isEmpty()) {
                        System.out.println("No available animals.");
                    } else {
                        availableAnimals.forEach(System.out::println);
                    }
                }
                case "5" -> {
                    System.out.print("Enter animal ID: ");
                    String id = scanner.nextLine().trim();

                    try {
                        shelter.markAsAdopted(id);
                        System.out.println("Animal marked as adopted.");
                    } catch (IllegalArgumentException exception) {
                        System.out.println(exception.getMessage());
                    }
                }
                default -> System.out.println("Invalid option. Try again");
            }
        }
    }

    private void printMenu(){
        System.out.println(" ");
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
//          Full logic for adding an animal
        Animal animal;
        switch (species.toLowerCase(Locale.ROOT))
        {
            case "dog" -> animal = new Dog(new AnimalId(), name, age);
            case "cat" -> animal = new Cat(new AnimalId(), name, age);
            case "bird" -> animal = new Bird(new AnimalId(), name, age);
            default -> {
                System.out.println("Unexpected species: " + species);
                return;
            }
        }

        shelter.addAnimal(animal);
        System.out.println("Animal added: " + animal);
    }
}
