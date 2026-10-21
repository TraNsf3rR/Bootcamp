package lv.bootcamp.shelter.service.data;

import java.nio.file.Path;

public record InvalidRow(Path sourceFile, int lineNumber, String reason) {
}
