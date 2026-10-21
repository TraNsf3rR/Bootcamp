package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.SpeciesSummary;
import lv.bootcamp.shelter.service.data.ShelterReportData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShelterAnalyticsServiceTest {

    @Test
    void buildsSpeciesCollectionsAndVetInputList() {
        LocalDate intakeDate = LocalDate.of(2026, 1, 15);
        List<Animal> animals = List.of(
                new Animal("Buddy", "Dog", OptionalInt.of(3), true, intakeDate),
                new Animal("Milo", "Cat", OptionalInt.empty(), true, intakeDate),
                new Animal("Max", "Dog", OptionalInt.of(5), false, intakeDate),
                new Animal("Pepper", "Bird", OptionalInt.empty(), false, intakeDate),
                new Animal("Rover", "Dog", OptionalInt.empty(), true, intakeDate)
        );
        ImportResult importResult = new ImportResult(animals, 0);

        ShelterReportData reportData = new ShelterAnalyticsService().buildReportData(importResult);

        assertSame(importResult, reportData.importResult());
        assertIterableEquals(List.of("Bird", "Cat", "Dog"), reportData.uniqueSpecies());
        assertIterableEquals(List.of("Bird", "Cat", "Dog"), reportData.animalsBySpecies().keySet());
        assertEquals(
                List.of("Buddy", "Max", "Rover"),
                reportData.animalsBySpecies().get("Dog").stream().map(Animal::getName).toList());
        assertEquals(
                List.of("Milo(Cat)", "Max(Dog)", "Pepper(Bird)", "Rover(Dog)"),
                reportData.animalsNeedingVetInput());

        assertIterableEquals(List.of("Bird", "Cat", "Dog"), reportData.speciesSummaries().keySet());

        SpeciesSummary dogSummary = reportData.speciesSummaries().get("Dog");
        assertEquals(3L, dogSummary.totalCount());
        assertEquals(2L, dogSummary.vaccinatedCount());
        assertEquals(1L, dogSummary.unvaccinatedCount());
        assertEquals("Max", dogSummary.oldestAnimal().orElseThrow().getName());
        assertEquals(5, dogSummary.oldestAnimal().orElseThrow().getAge().getAsInt());

        SpeciesSummary catSummary = reportData.speciesSummaries().get("Cat");
        assertEquals(1L, catSummary.totalCount());
        assertEquals(1L, catSummary.vaccinatedCount());
        assertEquals(0L, catSummary.unvaccinatedCount());
        assertTrue(catSummary.oldestAnimal().isEmpty());
    }
}
