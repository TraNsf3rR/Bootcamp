package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.DirectoryImportResult;
import lv.bootcamp.shelter.service.data.InvalidRow;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

@Slf4j
public class CsvImportService {

    private static final int EXPECTED_COLUMN_COUNT = 5;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.uuuu", Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);

    public ImportResult importAnimals(Path inputPath) throws IOException {
        return importFile(inputPath);
    }

    public ImportResult importAnimalsFromDirectory(Path inputDirectory) throws IOException {
        return importNewAnimalsFromDirectory(inputDirectory, Set.of()).importResult();
    }

    public DirectoryImportResult importNewAnimalsFromDirectory(
            Path inputDirectory, Set<String> previouslyProcessedFiles) throws IOException {
        if (!Files.isDirectory(inputDirectory)) {
            throw new NotDirectoryException(inputDirectory.toString());
        }

        List<Path> csvFiles;
        try (Stream<Path> paths = Files.walk(inputDirectory)) {
            csvFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(CsvImportService::isCsvFile)
                    .sorted()
                    .toList();
        }

        List<Animal> allAnimals = new ArrayList<>();
        List<InvalidRow> invalidRows = new ArrayList<>();
        List<String> processedFileNames = new ArrayList<>();
        Set<String> knownProcessedFiles = new TreeSet<>();
        previouslyProcessedFiles.stream()
                .map(CsvImportService::normalizePath)
                .forEach(knownProcessedFiles::add);

        for (Path csvFile : csvFiles) {
            String relativeFileName = getRelativeFileName(inputDirectory, csvFile);
            if (knownProcessedFiles.contains(relativeFileName)) {
                log.info("Skipping previously processed CSV file {}", csvFile);
                continue;
            }

            ImportResult fileResult = importFile(csvFile);
            allAnimals.addAll(fileResult.allAnimals());
            invalidRows.addAll(fileResult.invalidRows());
            processedFileNames.add(relativeFileName);
        }

        log.info("Finished import from {}: {} CSV files processed, {} animals imported, {} rows skipped",
                inputDirectory, processedFileNames.size(), allAnimals.size(), invalidRows.size());
        ImportResult importResult = new ImportResult(allAnimals, invalidRows);
        return new DirectoryImportResult(importResult, processedFileNames);
    }

    private ImportResult importFile(Path inputPath) throws IOException {
        log.info("Starting import from {}", inputPath);

        List<Animal> allAnimals = new ArrayList<>();
        List<InvalidRow> invalidRows = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            String row;
            while ((row = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    continue;
                }

                try {
                    allAnimals.add(parseAnimal(row));
                } catch (IllegalArgumentException | DateTimeParseException exception) {
                    invalidRows.add(new InvalidRow(inputPath, lineNumber, exception.getMessage()));
                    log.warn("Skipping invalid CSV row {} in {}: {}",
                            lineNumber, inputPath, exception.getMessage());
                }
            }
        }

        log.info("Finished import from {}: {} animals imported, {} rows skipped",
                inputPath, allAnimals.size(), invalidRows.size());
        return new ImportResult(allAnimals, invalidRows);
    }

    private static boolean isCsvFile(Path path) {
        return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv");
    }

    private String getRelativeFileName(Path inputDirectory, Path csvFile) {
        Path relativePath = inputDirectory.toAbsolutePath().normalize()
                .relativize(csvFile.toAbsolutePath().normalize());
        return normalizePath(relativePath.toString());
    }

    private static String normalizePath(String path) {
        return path.replace('\\', '/');
    }

    private Animal parseAnimal(String row) {
        String[] columns = row.split(",", -1);
        if (columns.length != EXPECTED_COLUMN_COUNT) {
            throw new IllegalArgumentException(
                    "expected " + EXPECTED_COLUMN_COUNT + " columns, found " + columns.length);
        }

        String name = requireValue(columns[0], "name");
        String species = requireValue(columns[1], "species");
        OptionalInt age = parseAge(columns[2]);
        boolean vaccinated = parseVaccinated(columns[3]);
        LocalDate intakeDate = LocalDate.parse(columns[4].trim(), DATE_FORMATTER);

        return new Animal(name, species, age, vaccinated, intakeDate);
    }

    private String requireValue(String value, String fieldName) {
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return trimmedValue;
    }

    private OptionalInt parseAge(String value) {
        String trimmedValue = value.trim();
        return trimmedValue.isEmpty()
                ? OptionalInt.empty()
                : OptionalInt.of(Integer.parseInt(trimmedValue));
    }

    private boolean parseVaccinated(String value) {
        String trimmedValue = value.trim();
        if ("true".equalsIgnoreCase(trimmedValue)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmedValue)) {
            return false;
        }
        throw new IllegalArgumentException("vaccinated must be true or false");
    }
}
