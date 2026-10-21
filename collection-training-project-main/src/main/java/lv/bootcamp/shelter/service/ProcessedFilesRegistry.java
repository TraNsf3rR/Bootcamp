package lv.bootcamp.shelter.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class ProcessedFilesRegistry {

    public Set<String> readProcessedFiles(Path registryPath) throws IOException {
        try {
            Set<String> processedFiles = new TreeSet<>();
            for (String line : Files.readAllLines(registryPath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    processedFiles.add(normalizePath(line));
                }
            }
            return processedFiles;
        } catch (NoSuchFileException exception) {
            return new TreeSet<>();
        }
    }

    public void saveProcessedFiles(Path registryPath, Collection<String> processedFiles)
            throws IOException {
        Path parentDirectory = registryPath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        Set<String> normalizedFiles = new TreeSet<>();
        for (String processedFile : processedFiles) {
            if (!processedFile.isBlank()) {
                normalizedFiles.add(normalizePath(processedFile));
            }
        }
        Files.write(registryPath, normalizedFiles, StandardCharsets.UTF_8);
    }

    private String normalizePath(String path) {
        return path.replace('\\', '/');
    }
}
