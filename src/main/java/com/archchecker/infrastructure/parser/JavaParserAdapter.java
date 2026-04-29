package com.archchecker.infrastructure.parser;

import com.archchecker.application.CodeParser;
import com.archchecker.domain.codebase.File;
import com.archchecker.domain.codebase.Project;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JavaParserAdapter implements CodeParser {
    private final JavaParser javaParser = new JavaParser();

    @Override
    public Project parseProject(Path rootPath) {
        if (!Files.isDirectory(rootPath)) {
            throw new IllegalArgumentException("Project path is not a directory: " + rootPath);
        }
        List<File> files = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(rootPath)) {
            stream.filter(p -> p.toString().endsWith(".java"))
                  .forEach(p -> files.add(toFile(p)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan project directory", e);
        }
        return new Project(rootPath, files);
    }

    private File toFile(Path javaFile) {
        try {
            ParseResult<CompilationUnit> result = javaParser.parse(javaFile);
            String pkg = result.getResult()
                    .flatMap(CompilationUnit::getPackageDeclaration)
                    .map(pd -> pd.getName().toString())
                    .orElse("");
            return new File(javaFile, pkg);
        } catch (IOException e) {
            return new File(javaFile, "");
        }
    }
}
