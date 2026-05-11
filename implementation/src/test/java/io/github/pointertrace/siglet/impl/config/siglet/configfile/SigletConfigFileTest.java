package io.github.pointertrace.siglet.impl.config.siglet.configfile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SigletConfigFileTest {

	@Test
	void parse_withConfigParserFactoryClass() {
		String yaml = """
				siglets:
				  - name: sample
				    description: sample description
				    siglet-class: io.github.pointertrace.siglet.SampleSpanlet
				    config-parser-factory-class: io.github.pointertrace.siglet.SampleConfigParserFactory
				    destinations:
				      - first
				      - second
				""";

		SigletConfigFile config = SigletConfigFile.parse(yaml);

		assertEquals(1, config.getSigletDefinitions().size());

		SigletConfigFile.SigletConfigFileDefinition definition = config.getSigletDefinitions().get(0);
		assertEquals("sample", definition.getName().getValue());
		assertEquals("sample description", definition.getDescription().getValue());
		assertEquals("io.github.pointertrace.siglet.SampleConfigParserFactory", definition.getConfigParserFactoryClassName().getValue());
		assertEquals("io.github.pointertrace.siglet.SampleSpanlet", definition.getSigletClassName().getValue());
		assertNull(definition.getConfigFactoryClassName());
		assertEquals(2, definition.getDestinations().size());
		assertEquals("first", definition.getDestinations().get(0).getValue());
		assertEquals("second", definition.getDestinations().get(1).getValue());
	}

	@Test
	void parse_withConfigFactoryClassOnly() {
		String yaml = """
				siglets:
				  - name: sample
				    description: sample description
				    siglet-class: io.github.pointertrace.siglet.SampleSpanlet
				    config-factory-class: io.github.pointertrace.siglet.SampleConfigFactory
				    destinations:
				      - first
				""";

		SigletConfigFile config = SigletConfigFile.parse(yaml);

		assertEquals(1, config.getSigletDefinitions().size());

		SigletConfigFile.SigletConfigFileDefinition definition = config.getSigletDefinitions().get(0);
		assertEquals("sample", definition.getName().getValue());
		assertEquals("sample description", definition.getDescription().getValue());
		assertEquals("io.github.pointertrace.siglet.SampleConfigFactory", definition.getConfigFactoryClassName().getValue());
		assertEquals("io.github.pointertrace.siglet.SampleSpanlet", definition.getSigletClassName().getValue());
		assertNull(definition.getConfigParserFactoryClassName());
	}

	@Test
	void parse_errorWhenBothConfigModesAreProvided() {
		String yaml = """
				siglets:
				  - name: sample
				    description: sample description
				    siglet-class: io.github.pointertrace.siglet.SampleSpanlet
				    config-parser-factory-class: io.github.pointertrace.siglet.SampleConfigParserFactory
				    config-factory-class: io.github.pointertrace.siglet.SampleConfigFactory
				    destinations:
				      - first
				""";

		IllegalArgumentException error = assertThrowsExactly(IllegalArgumentException.class,
				() -> SigletConfigFile.parse(yaml));

		assertEquals("Siglet sample must define exactly one config mode: " +
				"config-parser-factory-class or config-factory-class", error.getMessage());
	}

}