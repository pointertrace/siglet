package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.impl.utils.AbstractYamlErrorMessagesTest;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

public class YamlDescriptorErrorMessagesTest extends AbstractYamlErrorMessagesTest {

    @TestFactory
    public Stream<DynamicTest> errorMessagesTests() {
        return createTests("/yaml-descriptor-error-messages.yaml", YamlDescriptor::parse);
    }

}