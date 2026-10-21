package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.InvalidRow;
import lv.bootcamp.shelter.service.data.ImportResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvImportServiceTest {

    @TempDir
    private Path tempDirectory;

    private final CsvImportService importService = new CsvImportService();

    @Test
    void importsSampleFileAndCountsInvalidRows() throws IOException {
        Path samplePath = tempDirectory.resolve("sample-intake.csv");
        try (InputStream sample = Objects.requireNonNull(
                getClass().getResourceAsStream("/data/intake.csv"))) {
            Files.copy(sample, samplePath);
        }

        ImportResult result = importService.importAnimals(samplePath);

        assertEquals(31, result.allAnimals().size());
        assertEquals(6, result.skippedRows());
        assertEquals(
                List.of(8, 9, 10, 34, 35, 36),
                result.invalidRows().stream().map(InvalidRow::lineNumber).toList());
        assertEquals(3, result.allAnimals().stream().filter(animal -> animal.getAge().isEmpty()).count());
    }

    @Test
    void acceptsBlankAgeAndSkipsMalformedRows() throws IOException {
        Path inputPath = writeCsv(
                "name,species,age,vaccinated,intakeDate",
                "Mārtiņš,Dog,5,true,15.01.2026",
                "Milo,Cat,,false,16.01.2026",
                ",Dog,2,true,17.01.2026",
                "Luna,,2,true,18.01.2026",
                "Buddy,Dog,not-a-number,true,19.01.2026",
                "Max,Dog,2,maybe,20.01.2026",
                "Rex,Dog,4,true,32.02.2026",
                "Nova,Cat,1,true",
                "Pepper,Bird,3,false,21.01.2026,extra"
        );

        ImportResult result = importService.importAnimals(inputPath);

        assertEquals(2, result.allAnimals().size());
        assertEquals(7, result.skippedRows());
        assertEquals(
                List.of(4, 5, 6, 7, 8, 9, 10),
                result.invalidRows().stream().map(InvalidRow::lineNumber).toList());
        assertEquals(inputPath, result.invalidRows().get(0).sourceFile());
        assertEquals("name must not be blank", result.invalidRows().get(0).reason());

        Animal knownAgeAnimal = result.allAnimals().get(0);
        assertEquals("Mārtiņš", knownAgeAnimal.getName());
        assertEquals("Dog", knownAgeAnimal.getSpecies());
        assertEquals(5, knownAgeAnimal.getAge().getAsInt());
        assertTrue(knownAgeAnimal.isVaccinated());
        assertEquals(LocalDate.of(2026, 1, 15), knownAgeAnimal.getIntakeDate());

        Animal unknownAgeAnimal = result.allAnimals().get(1);
        assertEquals("Milo", unknownAgeAnimal.getName());
        assertEquals(OptionalInt.empty(), unknownAgeAnimal.getAge());
        assertFalse(unknownAgeAnimal.isVaccinated());
    }

    @Test
    void propagatesInputFileErrors() {
        Path missingPath = tempDirectory.resolve("missing.csv");

        assertThrows(IOException.class, () -> importService.importAnimals(missingPath));
    }

    private Path writeCsv(String... rows) throws IOException {
        Path inputPath = tempDirectory.resolve("intake.csv");
        String contents = String.join(System.lineSeparator(), rows) + System.lineSeparator();
        Files.writeString(inputPath, contents, StandardCharsets.UTF_8);
        return inputPath;
    }
}
