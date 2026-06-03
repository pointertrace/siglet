package io.github.pointertrace.siglet.impl.config.siglet.configfile;

import io.github.pointertrace.siglet.impl.utils.AbstractYamlErrorMessagesTest;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

public class SigletConfigFileErrorMessagesTest extends AbstractYamlErrorMessagesTest {

    @TestFactory
    public Stream<DynamicTest> errorMessagesTests() {
        return createTests("/siglet-config-file-error-messages.yaml", SigletConfigFile::parse);
    }
}
