package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.parser.Schema;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.object;

public class ExporterDescriptor extends ConfigurableDescriptor {

    public static Schema.Builder<?, ExporterDescriptor> descriptorSchemaBuilder(ExporterTypeRegistry registry) {
        return object(ExporterDescriptor::new)
                .addProperty(registry.getPropertySwitchSchema(ExporterDescriptor::setName, ExporterDescriptor::setType));
    }
}
