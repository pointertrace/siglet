package com.siglet.config;

import com.siglet.config.item.*;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.locatednode.Location;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.spanlet.filter.FilterConfig;
import com.siglet.spanlet.processor.ProcessorConfig;
import com.siglet.spanlet.router.Route;
import com.siglet.spanlet.router.RouterConfig;
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
    public void parseGrpcReceivers() {

        String config = """
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081""";


        ConfigNode node = configParser.parse(config);

        ConfigCheckFactory.receiversChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);

        var grpcReceiversArrays = assertInstanceOf(ArrayItem.class, value);
        assertEquals(Location.of(1, 1), grpcReceiversArrays.getLocation());

        var grpcReceivers = assertInstanceOf(List.class, grpcReceiversArrays.getValue());
        assertEquals(2, grpcReceivers.size());


        GrpcReceiverItem firstReceiver = assertInstanceOf(GrpcReceiverItem.class, grpcReceivers.getFirst());
        assertEquals("first", firstReceiver.getName().getValue());
        assertEquals(Location.of(1, 9), firstReceiver.getName().getLocation());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), firstReceiver.getAddress().getValue());
        assertEquals(Location.of(2, 12), firstReceiver.getAddress().getLocation());

        GrpcReceiverItem secondReceiver = assertInstanceOf(GrpcReceiverItem.class, grpcReceivers.get(1));
        assertEquals("second", secondReceiver.getName().getValue());
        assertEquals(Location.of(3, 9), secondReceiver.getName().getLocation());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081), secondReceiver.getAddress().getValue());
        assertEquals(Location.of(4, 12), secondReceiver.getAddress().getLocation());


    }

    @Test
    public void parseDebugReceivers() {

        String config = """
                - debug: first
                  address: direct:first
                - debug: second
                  address: direct:second""";

        ConfigNode node = configParser.parse(config);

        ConfigCheckFactory.receiversChecker().check(node);

        var value = node.getValue();

        assertNotNull(value);

        var debugReceiversArray = assertInstanceOf(ArrayItem.class, value);
        assertEquals(Location.of(1, 1), debugReceiversArray.getLocation());

        var debugReceivers = assertInstanceOf(List.class, debugReceiversArray.getValue());
        assertEquals(2, debugReceivers.size());
        DebugReceiverItem firstReceiver = assertInstanceOf(DebugReceiverItem.class, debugReceivers.getFirst());
        assertEquals(Location.of(1, 3), firstReceiver.getLocation());
        assertEquals("first", firstReceiver.getName().getValue());
        assertEquals(Location.of(1, 10), firstReceiver.getName().getLocation());
        assertEquals("direct:first", firstReceiver.getAddress().getValue());
        assertEquals(Location.of(2, 12), firstReceiver.getAddress().getLocation());

        DebugReceiverItem secondReceiver = assertInstanceOf(DebugReceiverItem.class, debugReceivers.get(1));
        assertEquals(Location.of(3, 3), secondReceiver.getLocation());
        assertEquals("second", secondReceiver.getName().getValue());
        assertEquals(Location.of(3, 10), secondReceiver.getName().getLocation());
        assertEquals("direct:second", secondReceiver.getAddress().getValue());
        assertEquals(Location.of(4, 12), secondReceiver.getAddress().getLocation());


    }

    @Test
    public void parseGrpcExporters() {

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
        var grpcExportersArray = assertInstanceOf(ArrayItem.class, value);
        assertEquals(Location.of(1,3), grpcExportersArray.getLocation());

        var grpcExporters = grpcExportersArray.getValue();
        assertEquals(2, grpcExporters.size());

        GrpcExporterItem firstExporter = assertInstanceOf(GrpcExporterItem.class, grpcExporters.getFirst());
        assertEquals("first", firstExporter.getName().getValue());
        assertEquals(Location.of(1,11), firstExporter.getName().getLocation());

        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080),
                firstExporter.getAddress().getValue());
        assertEquals(Location.of(2,14), firstExporter.getAddress().getLocation());


        GrpcExporterItem secondExporter = assertInstanceOf(GrpcExporterItem.class, grpcExporters.get(1));
        assertEquals("second", secondExporter.getName().getValue());
        assertEquals(Location.of(3,11), secondExporter.getName().getLocation());

        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081),
                secondExporter.getAddress().getValue());
        assertEquals(Location.of(4,14), secondExporter.getAddress().getLocation());


    }

    @Test
    public void parseDebugExporters() {

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
        var debugExportersArray = assertInstanceOf(ArrayItem.class, value);
        assertEquals(Location.of(1,3), debugExportersArray.getLocation());

        var debugExporters = debugExportersArray.getValue();
        assertEquals(2, debugExporters.size());

        DebugExporterItem firstExporter = assertInstanceOf(DebugExporterItem.class, debugExporters.getFirst());
        assertEquals("first", firstExporter.getName().getValue());
        assertEquals(Location.of(1,12), firstExporter.getName().getLocation());

        assertEquals("mock:first", firstExporter.getAddress().getValue());
        assertEquals(Location.of(2,14), firstExporter.getAddress().getLocation());


        DebugExporterItem secondExporter = assertInstanceOf(DebugExporterItem.class, debugExporters.get(1));
        assertEquals("second", secondExporter.getName().getValue());
        assertEquals(Location.of(3,12), secondExporter.getName().getLocation());

        assertEquals("mock:second", secondExporter.getAddress().getValue());
        assertEquals(Location.of(4,14), secondExporter.getAddress().getLocation());


    }

    @Test
    public void parseProcessorSpanlet() {

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

        ((ObjectConfigNode) node).adjustLocation();
        var value = node.getValue();

        assertNotNull(value);

        var spanletItem = assertInstanceOf(SpanletItem.class, value);
//        assertEquals(Location.of(1,1), spanletItem.getLocation());

        assertEquals("name-value", spanletItem.getName().getValue());
        assertEquals(Location.of(1,10), spanletItem.getName().getLocation());

        assertEquals(2,  spanletItem.getTo().getValue().size());

        assertEquals("first-destination", spanletItem.getTo().getValue().getFirst().getValue());
        assertEquals(Location.of(3,3), spanletItem.getTo().getValue().getFirst().getLocation());

        assertEquals("second-destination", spanletItem.getTo().getValue().get(1).getValue());
        assertEquals(Location.of(4,3), spanletItem.getTo().getValue().get(1).getLocation());


        assertEquals("processor", spanletItem.getType().getValue());
        assertEquals(Location.of(5,7), spanletItem.getType().getLocation());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, spanletItem.getConfig());
        assertEquals(Location.of(7,3), spanletItem.getConfig().getLocation());

        assertEquals("action-value", processorConfig.getAction().getValue());
        assertEquals(Location.of(7,11), processorConfig.getAction().getLocation());

    }

    @Test
    public void parseProcessorTracelet() {


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
        assertEquals("name-value", traceletItem.getName().getValue());
        assertEquals(List.of("first-destination", "second-destination"),
                traceletItem.getTo().getValue().stream().map(ValueItem::getValue).toList());
        assertEquals("processor", traceletItem.getType().getValue());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, traceletItem.getConfig());

        assertEquals("action-value", processorConfig.getAction().getValue());

    }

    @Test
    public void parseFilterSpanlet() {


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
        assertEquals("name-value", spanletItem.getName().getValue());
        assertEquals(List.of("destination-value"),
                spanletItem.getTo().getValue().stream().map(ValueItem::getValue).toList());
        assertEquals("filter", spanletItem.getType().getValue());

        var filterConfig = assertInstanceOf(FilterConfig.class, spanletItem.getConfig());

        assertEquals("expression-value", filterConfig.getExpression().getValue());

    }

    @Test
    public void parseFilterTracelet() {


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
        assertEquals("name-value", traceletItem.getName().getValue());
        assertEquals(List.of("destination-value"),
                traceletItem.getTo().getValue().stream().map(ValueItem::getValue).toList());
        assertEquals("filter", traceletItem.getType().getValue());

        var filterConfig = assertInstanceOf(FilterConfig.class, traceletItem.getConfig());

        assertEquals("expression-value", filterConfig.getExpression().getValue());

    }

    @Test
    public void parseRouterSpanlet() {


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


        assertEquals("name-value", spanletItem.getName().getValue());
        assertEquals(List.of("destination-value"),
                spanletItem.getTo().getValue().stream().map(ValueItem::getValue).toList());
        assertEquals("router", spanletItem.getType().getValue());

        var routerConfig = assertInstanceOf(RouterConfig.class, spanletItem.getConfig());
        assertEquals("default-destination", routerConfig.getDefaultRoute().getValue());
        assertEquals(2, routerConfig.getRoutes().getValue().size());

        Route firstClause = routerConfig.getRoutes().getValue().getFirst();
        assertEquals("first-clause-expression", firstClause.getExpression().getValue());
        assertEquals("fist-destination", firstClause.getTo().getValue());

        Route secondClause = routerConfig.getRoutes().getValue().get(1);
        assertEquals("second-clause-expression", secondClause.getExpression().getValue());
        assertEquals("second-destination", secondClause.getTo().getValue());


    }

    @Test
    public void parseRouterTracelet() {


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


        assertEquals("name-value", traceletItem.getName().getValue());
        assertEquals(List.of("destination-value"), traceletItem.getTo().getValue().stream().map(ValueItem::getValue).toList());
        assertEquals("router", traceletItem.getType().getValue());

        var routerConfig = assertInstanceOf(RouterConfig.class, traceletItem.getConfig());
        assertEquals("default-destination", routerConfig.getDefaultRoute().getValue());
        assertEquals(2, routerConfig.getRoutes().getValue().size());

        Route firstClause = routerConfig.getRoutes().getValue().getFirst();
        assertEquals("first-clause-expression", firstClause.getExpression().getValue());
        assertEquals("fist-destination", firstClause.getTo().getValue());

        Route secondClause = routerConfig.getRoutes().getValue().get(1);
        assertEquals("second-clause-expression", secondClause.getExpression().getValue());
        assertEquals("second-destination", secondClause.getTo().getValue());


    }

    @Test
    public void parseTraceAggregatorSpanlet() {


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
        assertEquals(List.of("destination-value"),
                traceAggregatorItem.getTo().getValue().stream().map(ValueItem::getValue).toList());
        assertEquals("default", traceAggregatorItem.getType().getValue());

        var traceAggregatorConfig = assertInstanceOf(TraceAggregatorConfig.class, traceAggregatorItem.getConfig());
        assertEquals(1, traceAggregatorConfig.getTimeoutMillis().getValue());
        assertEquals(2, traceAggregatorConfig.getInactiveTimeoutMillis().getValue());
        assertEquals("expression value", traceAggregatorConfig.getCompletionExpression().getValue());


    }

    @Test
    public void parseTracePipeline() {

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

        Item tracePipeline = node.getValue();
        tracePipeline.afterSetValues();

        assertNotNull(tracePipeline);
        var pipelinesArray = assertInstanceOf(ArrayItem.class, tracePipeline);
        var pipelines = pipelinesArray.getValue();
        assertEquals(1, pipelines.size());
        TracePipelineItem pipeline = assertInstanceOf(TracePipelineItem.class, pipelines.getFirst());

        assertEquals("name-value", pipeline.getName().getValue());
        List<SpanletItem> spanlets = pipeline.getProcessors().getValue().stream().toList();
        assertEquals(1, spanlets.size());
        List<String> start = pipeline.getStart().stream().map(ValueItem::getValue).toList();
        assertEquals(List.of("spanlet-name"), start);
        List<String> from = pipeline.getFrom().stream().map(ValueItem::getValue).toList();
        assertEquals(List.of("origin-value"), from);


        SpanletItem spanletItem = spanlets.getFirst();
        assertEquals("spanlet-name", spanletItem.getName().getValue());
        assertEquals(List.of("destination-value"),
                spanletItem.getTo().getValue().stream().map(ValueItem::getValue).toList());
        assertEquals("processor", spanletItem.getType().getValue());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, spanletItem.getConfig());
        assertEquals("action-value", processorConfig.getAction().getValue());

    }


    @Test
    public void parseGlobalConfig() {

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
        var receiverItemsArray = globalConfig.getReceivers();
        List<ReceiverItem> receiverItems = receiverItemsArray.getValue();
        assertEquals(2, receiverItems.size());
        GrpcReceiverItem firstReceiver = assertInstanceOf(GrpcReceiverItem.class, receiverItems.getFirst());
        assertEquals("first", firstReceiver.getName().getValue());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080),
                firstReceiver.getAddress().getValue());

        GrpcReceiverItem secondReceiver = assertInstanceOf(GrpcReceiverItem.class, receiverItems.get(1));
        assertEquals("second", secondReceiver.getName().getValue());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081),
                secondReceiver.getAddress().getValue());

        var exporterItemsArray = globalConfig.getExporters();
        List<ExporterItem> exporterItems = exporterItemsArray.getValue();
        assertEquals(2, exporterItems.size());
        GrpcExporterItem firstExporter = assertInstanceOf(GrpcExporterItem.class, exporterItems.getFirst());
        assertEquals("first", firstExporter.getName().getValue());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080),
                firstExporter.getAddress().getValue());

        GrpcExporterItem secondExporter = assertInstanceOf(GrpcExporterItem.class, exporterItems.get(1));
        assertEquals("second", secondExporter.getName().getValue());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081),
                secondExporter.getAddress().getValue());

        var pipelinesArray = globalConfig.getPipelines();
        var pipelines = pipelinesArray.getValue();
        assertEquals(1, pipelines.size());
        TracePipelineItem pipeline = pipelines.getFirst();
        assertEquals("pipeline name", pipeline.getName().getValue());

        List<SpanletItem> spanletItems = pipeline.getProcessors().getValue().stream().toList();
        assertEquals(1, spanletItems.size());
        SpanletItem spanletItem = spanletItems.getFirst();
        assertEquals("spanlet-name", spanletItem.getName().getValue());
        assertEquals(List.of("destination-value"),
                spanletItem.getTo().getValue().stream().map(ValueItem::getValue).toList());
        assertEquals("processor", spanletItem.getType().getValue());

        ProcessorConfig configBuilder = assertInstanceOf(ProcessorConfig.class, spanletItem.getConfig());
        assertEquals("action-value", configBuilder.getAction().getValue());


    }
}