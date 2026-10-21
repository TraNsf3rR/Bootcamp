package lv.bootcamp.shelter;

import lv.bootcamp.shelter.service.CsvImportService;
import lv.bootcamp.shelter.service.JsonSummaryExportService;
import lv.bootcamp.shelter.service.ProcessedFilesRegistry;
import lv.bootcamp.shelter.service.data.DirectoryImportResult;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.ReportExportService;
import lv.bootcamp.shelter.service.ShelterAnalyticsService;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

public class ShelterCsvApplication {

    public static void main(String[] args) throws IOException {
        Path inputDirectory = Path.of("src", "main", "resources", "data");
        Path outputPath = Path.of("output", "upload-report.txt");
        Path jsonSummaryPath = Path.of("output", "upload-summary.json");
        Path registryPath = Path.of("output", "processed-files.txt");

        CsvImportService importService = new CsvImportService();
        ProcessedFilesRegistry processedFilesRegistry = new ProcessedFilesRegistry();
        ShelterAnalyticsService analyticsService = new ShelterAnalyticsService();
        ReportExportService reportExportService = new ReportExportService();
        JsonSummaryExportService jsonSummaryExportService = new JsonSummaryExportService();

        Set<String> processedFileNames = new TreeSet<>(
                processedFilesRegistry.readProcessedFiles(registryPath));
        DirectoryImportResult directoryImportResult =
                importService.importNewAnimalsFromDirectory(inputDirectory, processedFileNames);
        ImportResult importResult = directoryImportResult.importResult();
        ShelterReportData reportData = analyticsService.buildReportData(importResult);
        reportExportService.writeReport(outputPath, reportData);
        jsonSummaryExportService.writeSummary(jsonSummaryPath, reportData);

        processedFileNames.addAll(directoryImportResult.processedFileNames());
        processedFilesRegistry.saveProcessedFiles(registryPath, processedFileNames);
    }
}
