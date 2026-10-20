package org.example.shelter;

import org.example.model.AdoptionStatus;
import org.example.model.Animal;

import java.util.ArrayList;
import java.util.List;

public class Shelter <T extends Animal>{
    private final List<T> animals = new ArrayList<>();

    public void addAnimal(T animal){
        animals.add(animal);
    }

    public List<T> getAllAnimals(){
        return List.copyOf(animals);
    }

    public List<T> findBySpecies(String species){
        return animals.stream()
                .filter(animal -> animal.getSpecies().equalsIgnoreCase(species.trim()))
                .toList();
    }

    public List<T> findAvailableAnimals(){
        return animals.stream()
                .filter(animal -> animal.getAdoptionStatus() == AdoptionStatus.AVAILABLE)
                .toList();
    }

    public void markAsAdopted(String id){
        for (T animal : animals)
        {
            if (animal.getId().toString().equals(id))
            {
                animal.markAsAdopted();
                return;
            }
        }
        System.out.println("No animal found with ID: " + id);
    }
}
