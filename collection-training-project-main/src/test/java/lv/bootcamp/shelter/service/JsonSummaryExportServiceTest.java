package lv.bootcamp.shelter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.InvalidRow;
import lv.bootcamp.shelter.service.data.ShelterReportData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonSummaryExportServiceTest {

    @TempDir
    private Path tempDirectory;

    @Test
    void writesUtf8JsonSummaryWithMetricsAndInvalidRows() throws IOException {
        LocalDate intakeDate = LocalDate.of(2026, 1, 15);
        ImportResult importResult = new ImportResult(List.of(
                new Animal("Buddy", "Dog", OptionalInt.of(3), true, intakeDate),
                new Animal("Mārtiņš", "Cat", OptionalInt.empty(), true, intakeDate),
                new Animal("Max", "Dog", OptionalInt.of(5), false, intakeDate)
        ), List.of(new InvalidRow(Path.of("intake.csv"), 8, "name must not be blank")));
        ShelterReportData reportData = new ShelterAnalyticsService().buildReportData(importResult);
        Path outputPath = tempDirectory.resolve("reports").resolve("upload-summary.json");

        new JsonSummaryExportService().writeSummary(outputPath, reportData);

        JsonNode json = new ObjectMapper().readTree(Files.readString(outputPath, StandardCharsets.UTF_8));
        String generatedDate = json.path("generated").asText();
        assertEquals(generatedDate, LocalDate.parse(generatedDate).toString());
        assertEquals(3, json.path("totalImported").asInt());
        assertEquals(1, json.path("totalSkipped").asInt());
        assertEquals(2, json.path("uniqueSpecies").size());
        assertEquals(List.of("Cat", "Dog"),
                List.of(json.path("uniqueSpecies").get(0).asText(),
                        json.path("uniqueSpecies").get(1).asText()));

        JsonNode species = json.path("species");
        assertEquals(2, species.size());
        assertEquals("Cat", species.get(0).path("name").asText());
        assertEquals(1, species.get(0).path("total").asLong());
        assertTrue(species.get(0).path("oldestAnimal").isNull());
        assertEquals("Dog", species.get(1).path("name").asText());
        assertEquals(1, species.get(1).path("vaccinated").asLong());
        assertEquals(1, species.get(1).path("unvaccinated").asLong());
        assertEquals("Max", species.get(1).path("oldestAnimal").path("name").asText());
        assertEquals(5, species.get(1).path("oldestAnimal").path("age").asInt());

        JsonNode invalidRow = json.path("invalidRows").get(0);
        assertEquals("intake.csv", invalidRow.path("sourceFile").asText());
        assertEquals(8, invalidRow.path("lineNumber").asInt());
        assertEquals("name must not be blank", invalidRow.path("reason").asText());
        assertEquals(2, json.path("animalsNeedingVetInput").size());
        assertEquals(List.of("Mārtiņš(Cat)", "Max(Dog)"),
                List.of(json.path("animalsNeedingVetInput").get(0).asText(),
                        json.path("animalsNeedingVetInput").get(1).asText()));
    }
}
