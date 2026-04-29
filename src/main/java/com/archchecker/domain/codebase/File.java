package com.archchecker.domain.codebase;

import java.nio.file.Files;
import java.nio.file.Path;

public class File {
    private final Path filePath;
    private final String packageName;

    public File(Path filePath, String packageName) {
        this.filePath = filePath;
        this.packageName = packageName == null ? "" : packageName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public String readContent() {
        try {
            return Files.readString(filePath);
        } catch (Exception e) {
            return "";
        }
    }
}
