package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalConfigDescriptorTest {



    @Test
    void parse() {

        var config = """
                queue-size: 1
                thread-pool-size: 2
                """;


        Schema schema = GlobalConfigDescriptor.descriptorSchemaBuilder().build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        GlobalConfigDescriptor globalConfigDescriptor = factory.create(GlobalConfigDescriptor.class);


        assertEquals(1, globalConfigDescriptor.getQueueSize().getValue().intValue());
        assertEquals(Location.of(1,13)  , globalConfigDescriptor.getQueueSize().getLocation());
        assertEquals(2, globalConfigDescriptor.getThreadPoolSize().getValue().intValue());
        assertEquals(Location.of(2,19)  , globalConfigDescriptor.getThreadPoolSize().getLocation());

    }


}