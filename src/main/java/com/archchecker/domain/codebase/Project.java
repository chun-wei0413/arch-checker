package com.archchecker.domain.codebase;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class Project {
    private final Path rootPath;
    private final List<File> files;

    public Project(Path rootPath, List<File> files) {
        this.rootPath = rootPath;
        this.files = files == null ? Collections.emptyList() : files;
    }

    public Path getRootPath() {
        return rootPath;
    }

    public List<File> listFiles() {
        return Collections.unmodifiableList(files);
    }
}
