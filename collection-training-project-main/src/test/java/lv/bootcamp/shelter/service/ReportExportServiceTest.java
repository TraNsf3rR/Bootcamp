package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.InvalidRow;
import lv.bootcamp.shelter.service.data.ImportResult;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportExportServiceTest {

    @TempDir
    private Path tempDirectory;

    @Test
    void writesUtf8ReportAndCreatesParentDirectories() throws IOException {
        LocalDate intakeDate = LocalDate.of(2026, 1, 15);
        ImportResult importResult = new ImportResult(List.of(
                new Animal("Buddy", "Dog", OptionalInt.of(3), true, intakeDate),
                new Animal("Mārtiņš", "Cat", OptionalInt.empty(), true, intakeDate),
                new Animal("Max", "Dog", OptionalInt.of(5), false, intakeDate)
        ), List.of(
                new InvalidRow(Path.of("intake.csv"), 8, "name must not be blank"),
                new InvalidRow(Path.of("intake.csv"), 9, "invalid age")
        ));
        ShelterReportData reportData = new ShelterAnalyticsService().buildReportData(importResult);
        Path outputPath = tempDirectory.resolve("reports").resolve("upload-report.txt");

        new ReportExportService().writeReport(outputPath, reportData);

        List<String> expectedLines = List.of(
                "Generated: " + LocalDate.now(),
                "Total imported: 3",
                "Total skipped: 2",
                "",
                "Unique species: Cat, Dog",
                "",
                "Per-species totals:",
                "Cat: total=1, vaccinated=1, unvaccinated=0, oldest=N/A",
                "Dog: total=2, vaccinated=1, unvaccinated=1, oldest=Max (age 5)",
                "",
                "Invalid rows:",
                "intake.csv:8 - name must not be blank",
                "intake.csv:9 - invalid age",
                "",
                "Needs vet input: Mārtiņš(Cat), Max(Dog)"
        );
        String expectedReport = String.join(System.lineSeparator(), expectedLines) + System.lineSeparator();

        assertEquals(expectedReport, Files.readString(outputPath, StandardCharsets.UTF_8));

        List<Path> archiveFiles = findArchiveFiles(outputPath);
        assertEquals(1, archiveFiles.size());
        assertTrue(archiveFiles.get(0).getFileName().toString()
                .matches("upload-report-\\d{8}-\\d{6}-\\d{3}(?:-\\d+)?\\.txt"));
        assertEquals(expectedReport, Files.readString(archiveFiles.get(0), StandardCharsets.UTF_8));
    }

    @Test
    void writesNoneForEmptySpeciesAndVetInputLists() throws IOException {
        ImportResult importResult = new ImportResult(List.of(), List.of());
        ShelterReportData reportData = new ShelterAnalyticsService().buildReportData(importResult);
        Path outputPath = tempDirectory.resolve("empty-report.txt");

        new ReportExportService().writeReport(outputPath, reportData);

        List<String> lines = Files.readAllLines(outputPath, StandardCharsets.UTF_8);
        assertTrue(lines.contains("Unique species: None"));
        int invalidRowsHeader = lines.indexOf("Invalid rows:");
        assertTrue(invalidRowsHeader >= 0);
        assertEquals("None", lines.get(invalidRowsHeader + 1));
        assertTrue(lines.contains("Needs vet input: None"));
    }

    @Test
    void createsDistinctTimestampedArchiveForEachReportRun() throws IOException {
        ImportResult importResult = new ImportResult(List.of(), List.of());
        ShelterReportData reportData = new ShelterAnalyticsService().buildReportData(importResult);
        Path outputPath = tempDirectory.resolve("upload-report.txt");
        ReportExportService exportService = new ReportExportService();

        exportService.writeReport(outputPath, reportData);
        exportService.writeReport(outputPath, reportData);

        List<Path> archiveFiles = findArchiveFiles(outputPath);
        assertEquals(2, archiveFiles.size());
        assertNotEquals(
                archiveFiles.get(0).getFileName().toString(),
                archiveFiles.get(1).getFileName().toString());
    }

    private List<Path> findArchiveFiles(Path outputPath) throws IOException {
        try (Stream<Path> files = Files.list(outputPath.getParent())) {
            return files
                    .filter(path -> path.getFileName().toString().startsWith("upload-report-"))
                    .sorted()
                    .toList();
        }
    }
}
