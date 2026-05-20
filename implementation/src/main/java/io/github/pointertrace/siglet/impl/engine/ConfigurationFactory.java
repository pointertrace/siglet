package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.api.SigletConfigFactory;
import io.github.pointertrace.siglet.api.SigletConfigParserFactory;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.Schema;
import io.github.pointertrace.siglet.parser.impl.schema.SchemaPropertyBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.any;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.object;
import static org.joor.Reflect.onClass;

public interface ConfigurationFactory<T> {

    Optional<Schema.Builder<?, T>> createConfigSchema();

    static ConfigurationFactory<Void> of() {
        return new EmptySchemaConfigFactory();
    }

    static <T> ConfigurationFactory<T> of(SigletConfigParserFactory<T> sigletConfigParserFactory) {
        return new SchemaConfigFactory<>(sigletConfigParserFactory);
    }

    static <T> ConfigurationFactory<T> of(SigletConfigFactory<T> sigletConfigFactory) {
        return new ConfigFactory<>(sigletConfigFactory);
    }

    static <T> ConfigurationFactory<T> of(List<SchemaPropertyBuilder<T, ?>> configSchema, Class<T> configClass) {
        return of(new SigletConfigParserFactory<T>() {

                      @Override
                      public List<SchemaPropertyBuilder<T, ?>> createConfigSchema() {
                          return configSchema;
                      }

                      @Override
                      public Class<T> getConfigClass() {
                          return configClass;
                      }
                  }
        );
    }

    class EmptySchemaConfigFactory implements ConfigurationFactory<Void> {

        @Override
        public Optional<Schema.Builder<?, Void>> createConfigSchema() {
            return Optional.empty();
        }
    }

    class SchemaConfigFactory<T> implements ConfigurationFactory<T> {

        private final SigletConfigParserFactory<T> sigletConfigParserFactory;

        public SchemaConfigFactory(SigletConfigParserFactory<T> sigletConfigParserFactory) {
            Objects.requireNonNull(sigletConfigParserFactory);
            this.sigletConfigParserFactory = sigletConfigParserFactory;
        }

        @Override
        public Optional<Schema.Builder<?, T>> createConfigSchema() {
            return Optional.of(object(ConfigurationFactory.getSupplierForNoArgsConstructor(sigletConfigParserFactory.getConfigClass()))
                    .addProperties(sigletConfigParserFactory.createConfigSchema()));
        }

    }

    class ConfigFactory<T> implements ConfigurationFactory<T> {

        private final SigletConfigFactory<T> sigletConfigFactory;

        public ConfigFactory(SigletConfigFactory<T> sigletConfigFactory) {
            Objects.requireNonNull(sigletConfigFactory);
            this.sigletConfigFactory = sigletConfigFactory;
        }

        public Function<Node, T> createNodeConfigFactory(SigletConfigFactory<T> configFactory) {
            return (node) -> configFactory.createConfig(node.toYaml());
        }

        @Override
        public Optional<Schema.Builder<?, T>> createConfigSchema() {

            SigletConfigFactory<T> configFactory = ConfigurationFactory.getSupplierForNoArgsConstructor(sigletConfigFactory.getClass()).get();

            return Optional.of(any(createNodeConfigFactory(configFactory)));
        }
    }

    private static <T> Supplier<T> getSupplierForNoArgsConstructor(Class<T> clazz) {
        return () -> {
            try {
                return onClass(clazz).create().get();
            } catch (Exception e) {
                throw new SigletError("Error creating instance of " + clazz.getName() + ": " + e.getMessage(), e);
            }
        };
    }
}
