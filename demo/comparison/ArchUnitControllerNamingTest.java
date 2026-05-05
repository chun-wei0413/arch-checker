package com.example.archunittest;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * ArchUnit version of "controller classes must end with 'Controller'".
 * The rule lives inside a JUnit test class — the team must depend on
 * ArchUnit + JUnit 5 just to express it, and only Java developers can
 * edit / extend it.
 */
@AnalyzeClasses(packages = "com.example")
class ControllerNamingTest {

    @ArchTest
    static final ArchRule controllers_must_have_Controller_suffix =
            classes()
                    .that().resideInAPackage("..controller..")
                    .and().areNotInterfaces()
                    .should().haveSimpleNameEndingWith("Controller")
                    .because("統一 controller 命名規範，避免後綴不一致造成維護困難");
}
