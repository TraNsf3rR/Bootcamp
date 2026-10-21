package lv.bootcamp.shelter.service.data;

import lv.bootcamp.shelter.model.Animal;

import java.util.List;

public record ImportResult(List<Animal> allAnimals, List<InvalidRow> invalidRows) {

    public ImportResult {
        allAnimals = List.copyOf(allAnimals);
        invalidRows = List.copyOf(invalidRows);
    }

    public int skippedRows() {
        return invalidRows.size();
    }
}
