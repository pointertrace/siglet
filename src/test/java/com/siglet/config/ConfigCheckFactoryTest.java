package com.siglet.config;

import com.siglet.config.item.*;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.spanlet.filter.FilterConfig;
import com.siglet.spanlet.processor.ProcessorConfig;
import com.siglet.spanlet.router.RouterConfig;
import com.siglet.spanlet.router.Route;
import com.siglet.spanlet.traceaggregator.TraceAggregatorConfig;
import com.siglet.spanlet.traceaggregator.TraceAggregatorItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.List;

import static com.siglet.config.ConfigCheckFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigCheckFactoryTest {

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

        ConfigCheckFactory.receiversChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);

        var grpcReceivers = assertInstanceOf(List.class, value);
        assertEquals(2, grpcReceivers.size());
        GrpcReceiverItem firstReceiver = assertInstanceOf(GrpcReceiverItem.class, grpcReceivers.get(0));
        assertEquals("first", firstReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstReceiver.getAddress());

        GrpcReceiverItem secondReceiver = assertInstanceOf(GrpcReceiverItem.class, grpcReceivers.get(1));
        assertEquals("second", secondReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondReceiver.getAddress());


    }

    @Test
    public void parseDebugReceivers() throws Exception {

        String config = """
                    - debug: first
                      address: direct:first
                    - debug: second
                      address: direct:second
                """;


        ConfigNode node = configParser.parse(config);

        ConfigCheckFactory.receiversChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);

        var debugReceivers = assertInstanceOf(List.class, value);
        assertEquals(2, debugReceivers.size());
        DebugReceiverItem firstReceiver = assertInstanceOf(DebugReceiverItem.class, debugReceivers.getFirst());
        assertEquals("first", firstReceiver.getName());
        assertEquals("direct:first", firstReceiver.getAddress());

        DebugReceiverItem secondReceiver = assertInstanceOf(DebugReceiverItem.class, debugReceivers.get(1));
        assertEquals("second", secondReceiver.getName());
        assertEquals("direct:second", secondReceiver.getAddress());


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

        ConfigCheckFactory.grpcExportersChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);
        var grpcExporters = assertInstanceOf(List.class, value);
        assertEquals(2, grpcExporters.size());
        GrpcExporterItem firstExporter = assertInstanceOf(GrpcExporterItem.class, grpcExporters.get(0));
        assertEquals("first", firstExporter.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstExporter.getAddress());

        GrpcExporterItem secondExporter = assertInstanceOf(GrpcExporterItem.class, grpcExporters.get(1));
        assertEquals("second", secondExporter.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondExporter.getAddress());


    }

    @Test
    public void parseDebugExporters() throws Exception {

        String config = """
                  - debug: first
                    address: mock:first
                  - debug: second
                    address: mock:second
                """;


        ConfigNode node = configParser.parse(config);

        ConfigCheckFactory.grpcExportersChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);
        var debugExporters = assertInstanceOf(List.class, value);
        assertEquals(2, debugExporters.size());
        DebugExporterItem firstExporter = assertInstanceOf(DebugExporterItem.class, debugExporters.getFirst());
        assertEquals("first", firstExporter.getName());
        assertEquals("mock:first", firstExporter.getAddress());

        DebugExporterItem secondExporter = assertInstanceOf(DebugExporterItem.class, debugExporters.get(1));
        assertEquals("second", secondExporter.getName());
        assertEquals("mock:second", secondExporter.getAddress());


    }
    @Test
    public void parseProcessorSpanlet() throws Exception {


        var config = """
                spanlet: name-value
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

        var spanletItem = assertInstanceOf(SpanletItem.class, value);
        assertEquals("name-value", spanletItem.getName());
        assertEquals(List.of("first-destination", "second-destination"), spanletItem.getTo());
        assertEquals("processor", spanletItem.getType());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, spanletItem.getConfig());

        assertEquals("action-value", processorConfig.getAction());

    }

    @Test
    public void parseProcessorTracelet() throws Exception {


        var config = """
                tracelet: name-value
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

        var traceletItem = assertInstanceOf(TraceletItem.class, value);
        assertEquals("name-value", traceletItem.getName());
        assertEquals(List.of("first-destination", "second-destination"), traceletItem.getTo());
        assertEquals("processor", traceletItem.getType());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, traceletItem.getConfig());

        assertEquals("action-value", processorConfig.getAction());

    }
    @Test
    public void parseFilterSpanlet() throws Exception {


        var config = """
                spanlet: name-value
                to: destination-value
                type: filter
                config:
                  expression: expression-value
                """;


        ConfigNode node = configParser.parse(config);

        spanletChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);
        var spanletItem = assertInstanceOf(SpanletItem.class, value);
        assertEquals("name-value", spanletItem.getName());
        assertEquals(List.of("destination-value"), spanletItem.getTo());
        assertEquals("filter", spanletItem.getType());

        var filterConfig = assertInstanceOf(FilterConfig.class, spanletItem.getConfig());

        assertEquals("expression-value", filterConfig.getExpression());

    }
    @Test
    public void parseFilterTracelet() throws Exception {


        var config = """
                tracelet: name-value
                to: destination-value
                type: filter
                config:
                  expression: expression-value
                """;


        ConfigNode node = configParser.parse(config);

        spanletChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);
        var traceletItem = assertInstanceOf(TraceletItem.class, value);
        assertEquals("name-value", traceletItem.getName());
        assertEquals(List.of("destination-value"), traceletItem.getTo());
        assertEquals("filter", traceletItem.getType());

        var filterConfig = assertInstanceOf(FilterConfig.class, traceletItem.getConfig());

        assertEquals("expression-value", filterConfig.getExpression());

    }

    @Test
    public void parseRouterSpanlet() throws Exception {


        var config = """
                spanlet: name-value
                to: destination-value
                type: router
                config:
                  default: default-destination
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
        var spanletItem = assertInstanceOf(SpanletItem.class, value);





        assertEquals("name-value", spanletItem.getName());
        assertEquals(List.of("destination-value"), spanletItem.getTo());
        assertEquals("router", spanletItem.getType());

        var routerConfig = assertInstanceOf(RouterConfig.class, spanletItem.getConfig());
        assertEquals("default-destination", routerConfig.getDefaultRoute());
        assertEquals(2, routerConfig.getRoutes().size());

        Route firstClause = routerConfig.getRoutes().getFirst();
        assertEquals("first-clause-expression", firstClause.getExpression());
        assertEquals("fist-destination", firstClause.getTo());

        Route secondClause = routerConfig.getRoutes().get(1);
        assertEquals("second-clause-expression", secondClause.getExpression());
        assertEquals("second-destination", secondClause.getTo());


    }

    @Test
    public void parseRouterTracelet() throws Exception {


        var config = """
                tracelet: name-value
                to: destination-value
                type: router
                config:
                  default: default-destination
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
        var traceletItem = assertInstanceOf(TraceletItem.class, value);


        assertEquals("name-value", traceletItem.getName());
        assertEquals(List.of("destination-value"), traceletItem.getTo());
        assertEquals("router", traceletItem.getType());

        var routerConfig = assertInstanceOf(RouterConfig.class, traceletItem.getConfig());
        assertEquals("default-destination", routerConfig.getDefaultRoute());
        assertEquals(2, routerConfig.getRoutes().size());

        Route firstClause = routerConfig.getRoutes().getFirst();
        assertEquals("first-clause-expression", firstClause.getExpression());
        assertEquals("fist-destination", firstClause.getTo());

        Route secondClause = routerConfig.getRoutes().get(1);
        assertEquals("second-clause-expression", secondClause.getExpression());
        assertEquals("second-destination", secondClause.getTo());


    }
    @Test
    public void parseTraceAggregatorSpanlet() throws Exception {


        var config = """
                trace-aggregator: name-value
                to: destination-value
                type: default
                config:
                  timeout-millis: 1
                  inactive-timeout-millis: 2
                  completion-expression: expression value
                """;


        ConfigNode node = configParser.parse(config);

        spanletChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);
        var traceAggregatorItem = assertInstanceOf(TraceAggregatorItem.class, value);
        assertEquals(List.of("destination-value"), traceAggregatorItem.getTo());
        assertEquals("default", traceAggregatorItem.getType());

        var traceAggregatorConfig = assertInstanceOf(TraceAggregatorConfig.class, traceAggregatorItem.getConfig());
        assertEquals(1, traceAggregatorConfig.getTimeoutMillis());
        assertEquals(2, traceAggregatorConfig.getInactiveTimeoutMillis());
        assertEquals("expression value", traceAggregatorConfig.getCompletionExpression());




    }

    @Test
    public void parseTracePipeline() throws Exception {

        var config = """
                - trace: name-value
                  from: origin-value
                  start: spanlet-name
                  pipeline:
                  - spanlet: spanlet-name
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                """;


        ConfigNode node = configParser.parse(config);

        tracePipelineChecker().check(node);

        Object tracePipeline = node.getValue();

        assertNotNull(tracePipeline);
        List<?> pipelines = assertInstanceOf(List.class, tracePipeline);
        assertEquals(1, pipelines.size());
        TracePipelineItem pipeline = assertInstanceOf(TracePipelineItem.class,pipelines.getFirst());

        assertEquals("name-value", pipeline.getName());
        List<SpanletItem> spanlets = pipeline.getProcessors();
        assertEquals(1, spanlets.size());
        List<String> start = pipeline.getStart();
        assertEquals(List.of("spanlet-name"), start);
        List<String> from = pipeline.getFrom();
        assertEquals(List.of("origin-value"), from);



        SpanletItem spanletItem = spanlets.getFirst();
        assertEquals("spanlet-name", spanletItem.getName());
        assertEquals(List.of("destination-value"), spanletItem.getTo());
        assertEquals("processor", spanletItem.getType());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, spanletItem.getConfig());
        assertEquals("action-value", processorConfig.getAction());

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
                  from: first
                  start: spanlet-name
                  pipeline:
                  - spanlet: spanlet-name
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                """;


        ConfigNode node = configParser.parse(config);

        globalConfigChecker().check(node);

        Object tracePipeline = node.getValue();

        assertNotNull(tracePipeline);
        var globalConfig = assertInstanceOf(ConfigItem.class, tracePipeline);
        List<ReceiverItem> receiverItems = globalConfig.getReceivers();
        assertEquals(2, receiverItems.size());
        GrpcReceiverItem firstReceiver = assertInstanceOf(GrpcReceiverItem.class, receiverItems.get(0));
        assertEquals("first", firstReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstReceiver.getAddress());

        GrpcReceiverItem secondReceiver = assertInstanceOf(GrpcReceiverItem.class, receiverItems.get(1));
        assertEquals("second", secondReceiver.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondReceiver.getAddress());

        List<ExporterItem> exporterItems = globalConfig.getExporters();
        assertEquals(2, exporterItems.size());
        GrpcExporterItem firstExporter = assertInstanceOf(GrpcExporterItem.class, exporterItems.get(0));
        assertEquals("first", firstExporter.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstExporter.getAddress());

        GrpcExporterItem secondExporter = assertInstanceOf(GrpcExporterItem.class, exporterItems.get(1));
        assertEquals("second", secondExporter.getName());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondExporter.getAddress());

        List<TracePipelineItem> pipelines = globalConfig.getPipelines();
        assertEquals(1, pipelines.size());
        TracePipelineItem pipeline = pipelines.get(0);
        assertEquals("pipeline name", pipeline.getName());

        List<SpanletItem> spanletItems = pipeline.getProcessors();
        assertEquals(1, spanletItems.size());
        SpanletItem spanletItem = spanletItems.get(0);
        assertEquals("spanlet-name", spanletItem.getName());
        assertEquals(List.of("destination-value"), spanletItem.getTo());
        assertEquals("processor", spanletItem.getType());

//        ProcessorConfigBuilder configBuilder = assertInstanceOf(ProcessorConfigBuilder.class, spanletItem.getConfig());
//        assertEquals("action-value", configBuilder.getAction());


    }
}