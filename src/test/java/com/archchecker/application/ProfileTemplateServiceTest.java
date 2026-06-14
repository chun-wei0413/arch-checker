package com.archchecker.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTemplateServiceTest {

    @Test
    void generateTemplateCreatesReadableYamlFile(@TempDir Path tmp) throws IOException {
        Path output = tmp.resolve("arch.yaml");
        ProfileTemplateService service = new ProfileTemplateService();

        service.generateTemplate(output);

        assertTrue(Files.exists(output), "template file should be created");
        String content = Files.readString(output);
        assertTrue(content.contains("name:"), "template must include 'name:' field");
        assertTrue(content.contains("rules:"), "template must include 'rules:' section");
    }

    @Test
    void generatedTemplateContainsAllFourRuleTypes(@TempDir Path tmp) throws IOException {
        Path output = tmp.resolve("arch.yaml");
        new ProfileTemplateService().generateTemplate(output);

        String content = Files.readString(output);
        assertTrue(content.contains("type: naming"), "must include naming rule example");
        assertTrue(content.contains("type: dependency"), "must include dependency rule example");
        assertTrue(content.contains("type: supertype"), "must include supertype rule example");
        assertTrue(content.contains("type: package"), "must include package rule example");
    }

    @Test
    void generatedTemplateIsLoadableByYamlProfileLoader(@TempDir Path tmp) throws IOException {
        Path output = tmp.resolve("arch.yaml");
        new ProfileTemplateService().generateTemplate(output);

        com.archchecker.infrastructure.profile.YamlProfileLoader loader =
                new com.archchecker.infrastructure.profile.YamlProfileLoader();
        com.archchecker.domain.profile.StyleProfile profile = loader.load(output);

        assertNotNull(profile);
        assertEquals(4, profile.getRules().size(), "template must produce 4 rules");
    }
}
