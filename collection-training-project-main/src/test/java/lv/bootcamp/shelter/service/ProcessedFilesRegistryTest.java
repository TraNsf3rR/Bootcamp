package lv.bootcamp.shelter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessedFilesRegistryTest {

    @TempDir
    private Path tempDirectory;

    private final ProcessedFilesRegistry registry = new ProcessedFilesRegistry();

    @Test
    void treatsMissingRegistryAsNoProcessedFiles() throws IOException {
        Path registryPath = tempDirectory.resolve("output").resolve("processed-files.txt");

        assertTrue(registry.readProcessedFiles(registryPath).isEmpty());
    }

    @Test
    void savesSortedUniquePathsAndReadsThemBack() throws IOException {
        Path registryPath = tempDirectory.resolve("output").resolve("processed-files.txt");

        registry.saveProcessedFiles(
                registryPath, List.of("nested\\second.csv", "first.csv", "first.csv", ""));

        assertEquals(Set.of("first.csv", "nested/second.csv"),
                registry.readProcessedFiles(registryPath));
        assertEquals(List.of("first.csv", "nested/second.csv"),
                Files.readAllLines(registryPath, StandardCharsets.UTF_8));
    }
}
