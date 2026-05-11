package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.parser.Schema;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.object;

public class ReceiverDescriptor extends ConfigurableDescriptor {


    public static Schema.Builder<?,ReceiverDescriptor> descriptorSchemaBuilder(ReceiverTypeRegistry registry) {
        return object(ReceiverDescriptor::new)
                .addProperty(registry.getPropertySwitchSchema(ReceiverDescriptor::setName, ReceiverDescriptor::setType));
    }

}