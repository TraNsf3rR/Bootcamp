package lv.bootcamp.shelter.service.data;

import lv.bootcamp.shelter.model.Animal;

import java.util.Optional;

public record SpeciesSummary(
        long totalCount,
        long vaccinatedCount,
        long unvaccinatedCount,
        Optional<Animal> oldestAnimal) {
}
