package io.github.pointertrace.siglet.impl.utils;

import io.github.pointertrace.siglet.api.SigletError;
import org.junit.jupiter.api.DynamicTest;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractYamlErrorMessagesTest {

    protected Stream<DynamicTest> createTests(String resourcePath, Consumer<String> parser) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new RuntimeException("Could not find " + resourcePath);
        }
        Map<String, Object> root = yaml.load(inputStream);
        Map<String, Object> scenarios = (Map<String, Object>) root.get("cenarios");

        List<DynamicTest> tests = new ArrayList<>();
        for (Map.Entry<String, Object> entry : scenarios.entrySet()) {
            String scenarioName = entry.getKey();
            Map<String, Object> scenarioData = (Map<String, Object>) entry.getValue();

            Object configYamlObj = scenarioData.get("siglet-config-yaml");
            String configYaml;
            if (configYamlObj instanceof String) {
                configYaml = (String) configYamlObj;
            } else if (configYamlObj == null) {
                configYaml = "";
            } else {
                configYaml = yaml.dump(configYamlObj);
            }

            String expectedErrorMessage = (String) scenarioData.get("error-message");

            tests.add(DynamicTest.dynamicTest(scenarioName, () -> {
                SigletError error = assertThrows(SigletError.class, () -> parser.accept(configYaml),
                        "Expected SigletError for scenario: " + scenarioName);
                String actual = error.getMessage().trim();
                String expected = expectedErrorMessage.trim();
                assertEquals(expected, actual,
                        "Error message mismatch for scenario: " + scenarioName + "\nExpected: " + expected + "\nActual: " + actual);
            }));
        }
        return tests.stream();
    }
}
