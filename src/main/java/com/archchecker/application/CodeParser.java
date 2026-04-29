package com.archchecker.application;

import com.archchecker.domain.codebase.Project;

import java.nio.file.Path;

public interface CodeParser {
    Project parseProject(Path rootPath);
}
