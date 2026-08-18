package io.github.pointertrace.siglet.integrationtests.bundle;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.siglet.ExampleJarsInfo;
import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.config.siglet.springboot.SpringBootBundleLoader;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ContextImpl;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultFactoryImpl;
import io.github.pointertrace.siglet.parser.Factory;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.Schema;
import io.github.pointertrace.siglet.parser.impl.YamlParser;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;
import static io.github.pointertrace.siglet.parser.impl.schema.SchemaObjectBuilder.object;
import static org.junit.jupiter.api.Assertions.*;

public class SpringBootBundleTest {


    private SpringBootBundleLoader springBootBundleLoader;

    private SpanAdapter spanAdapter;

    @BeforeEach
    void setUp() {
        springBootBundleLoader = new SpringBootBundleLoader();

        spanAdapter = new SpanAdapter(Span.newBuilder().setName("name").build(), Resource.newBuilder().build(),
                InstrumentationScope.newBuilder().build());
    }


    @Test
    void process() throws Exception {

        try (SigletBundle bundle = springBootBundleLoader.load(ExampleJarsInfo.getSpringBootExampleSigletFile())) {

            assertEquals(1, bundle.getDefinitions().size());
            SigletDefinition sigletDefinition = bundle.getDefinitions().getFirst();
            assertEquals("springboot-suffix-spanlet", sigletDefinition.getName());
            assertInstanceOf(Siglet.class, sigletDefinition.createProcessor());
            assertNotNull(sigletDefinition.createConfigurationFactory());

            Spanlet<Object> springBootSuffixSpanlet = assertInstanceOf(Spanlet.class, sigletDefinition.createProcessor());
            assertNotNull(springBootSuffixSpanlet);


            ConfigurationFactory configurationFactory = sigletDefinition.createConfigurationFactory();
            assertNotNull(configurationFactory);
            Optional<Schema.Builder<?, Object>> optionalSchemaBuilder = configurationFactory.createConfigSchema();
            assertTrue(optionalSchemaBuilder.isPresent());
            Schema schema = processorSchema(optionalSchemaBuilder.get());

            Node node = YamlParser.DEFAULT.parse("""
                    name: processor
                    config:
                      suffix: -a-suffix""");

            Factory factory = schema.validate(node);

            FatJarBundleTest.LocalProcessorConfig config = assertInstanceOf(FatJarBundleTest.LocalProcessorConfig.class, factory.create(FatJarBundleTest.LocalProcessorConfig.class));

            Context<Object> context = new ContextImpl<>(config.getConfig());

            springBootSuffixSpanlet.span(spanAdapter, context, ResultFactoryImpl.getInstance());

            assertEquals("name-a-suffix-springboot-uberjar", spanAdapter.getName());


        }
    }

    private Schema processorSchema(Schema.Builder<?, Object> configSchema) {
        return object(FatJarBundleTest.LocalProcessorConfig::new)
                .addProperty(property("name", FatJarBundleTest.LocalProcessorConfig::setName, string()))
                .addProperty(property("config", FatJarBundleTest.LocalProcessorConfig::setConfig, configSchema)).build();
    }

    public static class LocalProcessorConfig {

        private String name;

        private Object config;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getConfig() {
            return config;
        }

        public void setConfig(Object config) {
            this.config = config;
        }
    }


}
