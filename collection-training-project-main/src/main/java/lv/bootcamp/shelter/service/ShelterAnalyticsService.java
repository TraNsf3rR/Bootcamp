package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();

        Set<String> uniqueSpecies = new TreeSet<>();
        Map<String, List<Animal>> animalsBySpecies = new TreeMap<>();
        List<String> animalsNeedingVetInput = new ArrayList<>();

        for (Animal animal : allAnimals) {
            uniqueSpecies.add(animal.getSpecies());

            List<Animal> speciesAnimals = animalsBySpecies.computeIfAbsent(
                    animal.getSpecies(), species -> new ArrayList<>());
            speciesAnimals.add(animal);

            // Unvaccinated animals may need vaccine follow-up; unknown ages need assessment.
            // The OR includes either concern and adds an animal only once if both apply.
            if (!animal.isVaccinated() || animal.getAge() == null) {
                animalsNeedingVetInput.add(animal.getName() + "(" + animal.getSpecies() + ")");
            }
        }

        // TODO Step 3:
        // Use stream pipelines for:
        // - vaccinated vs unvaccinated counts per species
        // - oldest animal per species (excluding unknown ages)

        return new ShelterReportData(
                importResult, uniqueSpecies, animalsBySpecies, animalsNeedingVetInput);
    }
}
