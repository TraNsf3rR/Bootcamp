package lv.bootcamp.shelter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.service.data.ShelterJsonSummary;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class JsonSummaryExportService {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public void writeSummary(Path outputPath, ShelterReportData reportData) throws IOException {
        Path parentDirectory = outputPath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ShelterJsonSummary summary = toJsonSummary(reportData);
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            OBJECT_MAPPER.writeValue(writer, summary);
        }

        log.info("Wrote shelter JSON summary to {}", outputPath);
    }

    private ShelterJsonSummary toJsonSummary(ShelterReportData reportData) {
        List<ShelterJsonSummary.Species> species = reportData.speciesSummaries().entrySet().stream()
                .map(entry -> {
                    var summary = entry.getValue();
                    ShelterJsonSummary.OldestAnimal oldestAnimal = summary.oldestAnimal()
                            .map(animal -> new ShelterJsonSummary.OldestAnimal(
                                    animal.getName(), animal.getAge().getAsInt()))
                            .orElse(null);

                    return new ShelterJsonSummary.Species(
                            entry.getKey(),
                            summary.totalCount(),
                            summary.vaccinatedCount(),
                            summary.unvaccinatedCount(),
                            oldestAnimal);
                })
                .toList();

        List<ShelterJsonSummary.InvalidRow> invalidRows =
                reportData.importResult().invalidRows().stream()
                        .map(invalidRow -> new ShelterJsonSummary.InvalidRow(
                                invalidRow.sourceFile().toString(),
                                invalidRow.lineNumber(),
                                invalidRow.reason()))
                        .toList();

        return new ShelterJsonSummary(
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                reportData.importResult().allAnimals().size(),
                reportData.importResult().skippedRows(),
                List.copyOf(reportData.uniqueSpecies()),
                species,
                invalidRows,
                reportData.animalsNeedingVetInput());
    }
}
