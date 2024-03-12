package com.siglet.config.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.siglet.config.GrpcReceiver;
import com.siglet.config.parser.CustomJsonNodeFactory;
import com.siglet.config.parser.CustomParserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GrpcReceiverListFactoryTest {


    private GrpcReceiverListFactory grpcReceiverListFactory;

    @BeforeEach
    void setUp() {
        grpcReceiverListFactory = new GrpcReceiverListFactory();
    }

    @Test
    public void create() throws Exception {
        String yaml = """
                - name: first-name
                  address: first:8081
                - name: second-name
                  address: second:8082
                """;

//        CustomParserFactory customParserFactory = new CustomParserFactory();
//        ObjectMapper om = new ObjectMapper(customParserFactory);
//        var factory = new CustomJsonNodeFactory(om.getDeserializationConfig().getNodeFactory(),
//                customParserFactory);
//        om.setConfig(om.getDeserializationConfig().with(factory));
//
//        YAMLMapper.builder().add
//
//
//
//
//
//
//        var config = Configuration.builder()
//                .mappingProvider(new JacksonMappingProvider(om))
//                .jsonProvider(new JacksonY JacksonJsonNodeJsonProvider(om))
//                .options(Option.ALWAYS_RETURN_LIST)
//                .build();
//        ArrayNode node = (ArrayNode) mapper.readTree(yaml);
//        node.get
        ObjectMapper mapper = new ObjectMapper(new CustomParserFactory());
        JsonNode node = mapper.readTree(yaml);

        System.out.println(node);

        List<GrpcReceiver> grpcReceivers = grpcReceiverListFactory.create(new Yaml().load(yaml));

        assertEquals(2, grpcReceivers.size());
        assertEquals("first-name",grpcReceivers.get(0).getName());
        assertEquals(InetSocketAddress.createUnresolved("first", 8081),grpcReceivers.get(0).getAddress());

        assertEquals("second-name",grpcReceivers.get(1).getName());
        assertEquals(InetSocketAddress.createUnresolved("second", 8082),grpcReceivers.get(1).getAddress());

    }
}