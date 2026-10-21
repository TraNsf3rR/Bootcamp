package lv.bootcamp.shelter.service.data;

import java.util.List;

public record DirectoryImportResult(
        ImportResult importResult,
        List<String> processedFileNames) {

    public DirectoryImportResult {
        processedFileNames = List.copyOf(processedFileNames);
    }
}
