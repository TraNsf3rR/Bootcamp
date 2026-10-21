package lv.bootcamp.shelter.service.data;

import java.util.List;

public record ShelterJsonSummary(
        String generated,
        int totalImported,
        int totalSkipped,
        List<String> uniqueSpecies,
        List<Species> species,
        List<InvalidRow> invalidRows,
        List<String> animalsNeedingVetInput) {

    public ShelterJsonSummary {
        uniqueSpecies = List.copyOf(uniqueSpecies);
        species = List.copyOf(species);
        invalidRows = List.copyOf(invalidRows);
        animalsNeedingVetInput = List.copyOf(animalsNeedingVetInput);
    }

    public record Species(
            String name,
            long total,
            long vaccinated,
            long unvaccinated,
            OldestAnimal oldestAnimal) {
    }

    public record OldestAnimal(String name, int age) {
    }

    public record InvalidRow(String sourceFile, int lineNumber, String reason) {
    }
}
