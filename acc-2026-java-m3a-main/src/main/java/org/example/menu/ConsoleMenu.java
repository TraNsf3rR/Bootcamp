package org.example.menu;

import org.example.model.Animal;
import org.example.shelter.Shelter;

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
                case "1" -> {
                    System.out.println("Add animal not implemented yet");
                }
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
}
