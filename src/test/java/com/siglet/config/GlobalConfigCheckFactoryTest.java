package com.siglet.config;

import com.siglet.config.builder.*;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.filter.FilterConfigBuilder;
import com.siglet.spanlet.processor.ProcessorConfigBuilder;
import com.siglet.spanlet.router.RouterConfigBuilder;
import com.siglet.spanlet.router.RouterItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.List;

import static com.siglet.config.GlobalConfigCheckFactory.*;
import static com.siglet.config.parser.schema.SchemaFactory.*;
import static com.siglet.config.parser.schema.SchemaFactory.text;
import static org.junit.jupiter.api.Assertions.*;

class GlobalConfigCheckFactoryTest {

    private ConfigParser configParser;

    @BeforeEach
    public void setUp() {
        configParser = new ConfigParser();
    }

    @Test
    public void parseGrpcReceivers() throws Exception {

        String config = """
                    - grpc: first
                      address: localhost:8080
                    - grpc: second
                      address: localhost:8081
                """;


        ConfigNode node = configParser.parse(config);

        GlobalConfigCheckFactory.receiversChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);

        var grpcReceivers = assertInstanceOf(List.class, value);
        assertEquals(2, grpcReceivers.size());
        GrpcReceiverBuilder firstReceiver = assertInstanceOf(GrpcReceiverBuilder.class, grpcReceivers.get(0));
        assertEquals("first", firstReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstReceiver.getAddress());

        GrpcReceiverBuilder secondReceiver = assertInstanceOf(GrpcReceiverBuilder.class, grpcReceivers.get(1));
        assertEquals("second", secondReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondReceiver.getAddress());


    }

    @Test
    public void parseGrpcExporters() throws Exception {

        String config = """
                  - grpc: first
                    address: localhost:8080
                  - grpc: second
                    address: localhost:8081
                """;


        ConfigNode node = configParser.parse(config);

        GlobalConfigCheckFactory.grpcExportersChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);
        var grpcExporters = assertInstanceOf(List.class, value);
        assertEquals(2, grpcExporters.size());
        GrpcExporterBuilder firstExporter = assertInstanceOf(GrpcExporterBuilder.class, grpcExporters.get(0));
        assertEquals("first", firstExporter.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstExporter.getAddress());

        GrpcExporterBuilder secondExporter = assertInstanceOf(GrpcExporterBuilder.class, grpcExporters.get(1));
        assertEquals("second", secondExporter.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondExporter.getAddress());


    }

    @Test
    public void parseProcessorSpanlet() throws Exception {


        var config = """
                spanlet: name-value
                from:
                - first-origin
                - second-origin
                to:
                - first-destination
                - second-destination
                type: processor
                config:
                  action: action-value
                """;


        ConfigNode node = configParser.parse(config);

        spanletChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);

        var spanlet = assertInstanceOf(SpanletBuilder.class, value);
        assertEquals("name-value", spanlet.getName());
        assertEquals(List.of("first-destination","second-destination"), spanlet.getTo());
        assertEquals(List.of("first-origin","second-origin"), spanlet.getFrom());
        assertEquals("processor", spanlet.getType());

        var processorConfigBuilder = assertInstanceOf(ProcessorConfigBuilder.class, spanlet.getConfig());

        assertEquals("action-value", processorConfigBuilder.getAction());

    }

    @Test
    public void parseFilterSpanlet() throws Exception {


        var config = """
                spanlet: name-value
                from: origin-value
                to: destination-value
                type: filter
                config:
                  expression: expression-value
                """;


        ConfigNode node = configParser.parse(config);

        spanletChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);
        var spanlet = assertInstanceOf(SpanletBuilder.class, value);
        assertEquals("name-value", spanlet.getName());
        assertEquals(List.of("destination-value"), spanlet.getTo());
        assertEquals("filter", spanlet.getType());

        var filterConfigBuilder = assertInstanceOf(FilterConfigBuilder.class, spanlet.getConfig());

        assertEquals("expression-value", filterConfigBuilder.getExpression());

    }

    @Test
    public void parseRouterSpanlet() throws Exception {


        var config = """
                spanlet: name-value
                from: origin-value
                to: destination-value
                type: router
                config:
                  routes:
                    - when: first-clause-expression
                      to: fist-destination
                    - when: second-clause-expression
                      to: second-destination
                """;


        ConfigNode node = configParser.parse(config);

        spanletChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);
        var spanlet = assertInstanceOf(SpanletBuilder.class, value);
        assertEquals("name-value", spanlet.getName());
        assertEquals(List.of("destination-value"), spanlet.getTo());
        assertEquals(List.of("origin-value"), spanlet.getFrom());
        assertEquals("router", spanlet.getType());

        var routerConfigBuilder = assertInstanceOf(RouterConfigBuilder.class, spanlet.getConfig());
        assertEquals(2, routerConfigBuilder.getRouters().size());

        RouterItem firstClause = routerConfigBuilder.getRouters().get(0);
        assertEquals("first-clause-expression", firstClause.getExpression());
        assertEquals("fist-destination", firstClause.getTo());

        RouterItem secondClause = routerConfigBuilder.getRouters().get(1);
        assertEquals("second-clause-expression", secondClause.getExpression());
        assertEquals("second-destination", secondClause.getTo());


    }


    @Test
    public void parseTracePipeline() throws Exception {

        var config = """
                - trace: name-value
                  pipeline:
                  - spanlet: spanlet-name
                    from: origin-value
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                """;


        ConfigNode node = configParser.parse(config);

        tracePipelineChecker().check(node);

        Object tracePipeline = node.getValue();

        assertNotNull(tracePipeline);
        var pipelines = assertInstanceOf(List.class, tracePipeline);
        assertEquals(1, pipelines.size());
        var pipeline = assertInstanceOf(TracePipelineBuilder.class, pipelines.get(0));
        assertEquals("name-value", pipeline.getName());
        var spanlets = pipeline.getSpanletBuilders();
        assertEquals(1, spanlets.size());

        var spanlet = spanlets.get(0);
        assertEquals("spanlet-name", spanlet.getName());
        assertEquals(List.of("destination-value"), spanlet.getTo());
        assertEquals("processor", spanlet.getType());

        var configBuilder = assertInstanceOf(ProcessorConfigBuilder.class, spanlet.getConfig());
        assertEquals("action-value", configBuilder.getAction());

    }


    @Test
    public void parseGlobalConfig() throws Exception {

        var config = """
                receivers:
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081
                exporters:
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081
                pipelines:
                - trace: pipeline name
                  pipeline:
                  - spanlet: spanlet-name
                    from: origin value
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                """;


        ConfigNode node = configParser.parse(config);

        globalConfigChecker().check(node);

        Object tracePipeline = node.getValue();

        assertNotNull(tracePipeline);
        var globalConfig = assertInstanceOf(GlobalConfigBuilder.class, tracePipeline);
        List<ReceiverBuilder> receivers = globalConfig.getReceivers();
        assertEquals(2, receivers.size());
        GrpcReceiverBuilder firstReceiver = assertInstanceOf(GrpcReceiverBuilder.class, receivers.get(0));
        assertEquals("first", firstReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstReceiver.getAddress());

        GrpcReceiverBuilder secondReceiver = assertInstanceOf(GrpcReceiverBuilder.class, receivers.get(1));
        assertEquals("second", secondReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondReceiver.getAddress());

        List<ExporterBuilder> exporters = globalConfig.getExporters();
        assertEquals(2, exporters.size());
        GrpcExporterBuilder firstExporter = assertInstanceOf(GrpcExporterBuilder.class, exporters.get(0));
        assertEquals("first", firstExporter.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstExporter.getAddress());

        GrpcExporterBuilder secondExporter = assertInstanceOf(GrpcExporterBuilder.class, exporters.get(1));
        assertEquals("second", secondExporter.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondExporter.getAddress());

        List<TracePipelineBuilder> pipelines = globalConfig.getPipelines();
        assertEquals(1, pipelines.size());
        TracePipelineBuilder pipeline = pipelines.get(0);
        assertEquals("pipeline name", pipeline.getName());

        List<SpanletBuilder> spanlets = pipeline.getSpanletBuilders();
        assertEquals(1, spanlets.size());
        SpanletBuilder spanlet = spanlets.get(0);
        assertEquals("spanlet-name", spanlet.getName());
        assertEquals(List.of("destination-value"), spanlet.getTo());
        assertEquals("processor", spanlet.getType());

        ProcessorConfigBuilder configBuilder = assertInstanceOf(ProcessorConfigBuilder.class, spanlet.getConfig());
        assertEquals("action-value", configBuilder.getAction());


    }
}