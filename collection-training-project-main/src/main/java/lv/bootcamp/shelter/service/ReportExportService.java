package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.service.data.InvalidRow;
import lv.bootcamp.shelter.service.data.SpeciesSummary;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class ReportExportService {

    public void writeReport(Path outputPath, ShelterReportData reportData) throws IOException {
        Path parentDirectory = outputPath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writeLine(writer, "Generated: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            writeLine(writer, "Total imported: " + reportData.importResult().allAnimals().size());
            writeLine(writer, "Total skipped: " + reportData.importResult().skippedRows());
            writeLine(writer, "");

            String uniqueSpecies = reportData.uniqueSpecies().isEmpty()
                    ? "None"
                    : String.join(", ", reportData.uniqueSpecies());
            writeLine(writer, "Unique species: " + uniqueSpecies);
            writeLine(writer, "");
            writeLine(writer, "Per-species totals:");

            for (Map.Entry<String, SpeciesSummary> entry : reportData.speciesSummaries().entrySet()) {
                SpeciesSummary summary = entry.getValue();
                String oldestAnimal = summary.oldestAnimal()
                        .map(animal -> animal.getName() + " (age " + animal.getAge().getAsInt() + ")")
                        .orElse("N/A");

                writeLine(writer, entry.getKey() + ": total=" + summary.totalCount()
                        + ", vaccinated=" + summary.vaccinatedCount()
                        + ", unvaccinated=" + summary.unvaccinatedCount()
                        + ", oldest=" + oldestAnimal);
            }

            writeLine(writer, "");
            writeLine(writer, "Invalid rows:");
            if (reportData.importResult().invalidRows().isEmpty()) {
                writeLine(writer, "None");
            } else {
                for (InvalidRow invalidRow : reportData.importResult().invalidRows()) {
                    writeLine(writer, invalidRow.sourceFile() + ":" + invalidRow.lineNumber()
                            + " - " + invalidRow.reason());
                }
            }

            writeLine(writer, "");
            String animalsNeedingVetInput = reportData.animalsNeedingVetInput().isEmpty()
                    ? "None"
                    : String.join(", ", reportData.animalsNeedingVetInput());
            writeLine(writer, "Needs vet input: " + animalsNeedingVetInput);
        }

        log.info("Wrote shelter report to {}", outputPath);
    }

    private void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }
}
