package com.siglet.config;

import com.siglet.config.item.*;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.located.Location;
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
        assertEquals(Location.of(1,1), spanletItem.getLocation());

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
        assertEquals(Location.of(6,1), spanletItem.getConfig().getLocation());

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
        assertEquals(Location.of(1,1), traceletItem.getLocation());

        assertEquals("name-value", traceletItem.getName().getValue());
        assertEquals(Location.of(1,11), traceletItem.getName().getLocation());

        assertEquals(2 , traceletItem.getTo().getValue().size());

        assertEquals("first-destination", traceletItem.getTo().getValue().get(0).getValue());
        assertEquals(Location.of(3,3), traceletItem.getTo().getValue().get(0).getLocation());

        assertEquals("second-destination", traceletItem.getTo().getValue().get(1).getValue());
        assertEquals(Location.of(4,3), traceletItem.getTo().getValue().get(1).getLocation());

        assertEquals("processor", traceletItem.getType().getValue());
        assertEquals(Location.of(5,7), traceletItem.getType().getLocation());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, traceletItem.getConfig());
        assertEquals(Location.of(6,1), traceletItem.getConfig().getLocation());

        assertEquals("action-value", processorConfig.getAction().getValue());
        assertEquals(Location.of(7,11), processorConfig.getAction().getLocation());

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
        assertEquals(Location.of(1,1), spanletItem.getLocation());

        assertEquals("name-value", spanletItem.getName().getValue());
        assertEquals(Location.of(1,10), spanletItem.getName().getLocation());

        assertEquals(1, spanletItem.getTo().getValue().size());
        assertEquals("destination-value", spanletItem.getTo().getValue().getFirst().getValue());
        assertEquals(Location.of(2,5), spanletItem.getTo().getValue().getFirst().getLocation());

        assertEquals("filter", spanletItem.getType().getValue());
        assertEquals(Location.of(3,7), spanletItem.getType().getLocation());

        var filterConfig = assertInstanceOf(FilterConfig.class, spanletItem.getConfig());
        assertEquals(Location.of(4,1), filterConfig.getLocation());

        assertEquals("expression-value", filterConfig.getExpression().getValue());
        assertEquals(Location.of(5,15), filterConfig.getExpression().getLocation());

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
        assertEquals(Location.of(1,1), traceletItem.getLocation());

        assertEquals(Location.of(1,11), traceletItem.getName().getLocation());
        assertEquals("name-value", traceletItem.getName().getValue());
        assertEquals(Location.of(1,11), traceletItem.getName().getLocation());

        assertEquals(1, traceletItem.getTo().getValue().size());
        assertEquals("destination-value", traceletItem.getTo().getValue().getFirst().getValue());
        assertEquals(Location.of(2,5), traceletItem.getTo().getLocation());

        assertEquals("filter", traceletItem.getType().getValue());
        assertEquals(Location.of(3,7), traceletItem.getType().getLocation());

        var filterConfig = assertInstanceOf(FilterConfig.class, traceletItem.getConfig());
        assertEquals(Location.of(4,1), filterConfig.getLocation());

        assertEquals("expression-value", filterConfig.getExpression().getValue());
        assertEquals(Location.of(5,15), filterConfig.getExpression().getLocation());


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
        assertEquals(Location.of(1,1), spanletItem.getLocation());


        assertEquals("name-value", spanletItem.getName().getValue());
        assertEquals(Location.of(1,10), spanletItem.getName().getLocation());

        assertEquals(1, spanletItem.getTo().getValue().size());
        assertEquals("destination-value", spanletItem.getTo().getValue().getFirst().getValue());
        assertEquals(Location.of(2,5), spanletItem.getTo().getLocation());

        assertEquals("router", spanletItem.getType().getValue());
        assertEquals(Location.of(3,7), spanletItem.getType().getLocation());

        var routerConfig = assertInstanceOf(RouterConfig.class, spanletItem.getConfig());
        assertEquals(Location.of(4,1), routerConfig.getLocation());

        assertEquals("default-destination", routerConfig.getDefaultRoute().getValue());
        assertEquals(Location.of(5,12), routerConfig.getDefaultRoute().getLocation());

        assertEquals(2, routerConfig.getRoutes().getValue().size());
        assertEquals(Location.of(6,3), routerConfig.getRoutes().getLocation());

        Route firstClause = routerConfig.getRoutes().getValue().getFirst();
        assertEquals(Location.of(7,7), firstClause.getLocation());
        assertEquals("first-clause-expression", firstClause.getExpression().getValue());
        assertEquals(Location.of(7,13), firstClause.getExpression().getLocation());
        assertEquals("fist-destination", firstClause.getTo().getValue());
        assertEquals(Location.of(8,11), firstClause.getTo().getLocation());

        Route secondClause = routerConfig.getRoutes().getValue().get(1);
        assertEquals(Location.of(9,7), secondClause.getLocation());
        assertEquals("second-clause-expression", secondClause.getExpression().getValue());
        assertEquals(Location.of(9,13), secondClause.getExpression().getLocation());
        assertEquals("second-destination", secondClause.getTo().getValue());
        assertEquals(Location.of(10,11), secondClause.getTo().getLocation());


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
        assertEquals(Location.of(1, 1), traceletItem.getLocation());


        assertEquals("name-value", traceletItem.getName().getValue());
        assertEquals(Location.of(1, 11), traceletItem.getName().getLocation());

        assertEquals(1, traceletItem.getTo().getValue().size());
        assertEquals("destination-value", traceletItem.getTo().getValue().getFirst().getValue());
        assertEquals(Location.of(1, 11), traceletItem.getName().getLocation());

        assertEquals("router", traceletItem.getType().getValue());
        assertEquals(Location.of(3, 7), traceletItem.getType().getLocation());


        var routerConfig = assertInstanceOf(RouterConfig.class, traceletItem.getConfig());
        assertEquals(Location.of(4, 1), routerConfig.getLocation());

        assertEquals("default-destination", routerConfig.getDefaultRoute().getValue());
        assertEquals(Location.of(5, 12), routerConfig.getDefaultRoute().getLocation());

        assertEquals(2, routerConfig.getRoutes().getValue().size());
        assertEquals(Location.of(6, 3), routerConfig.getRoutes().getLocation());

        Route firstClause = routerConfig.getRoutes().getValue().getFirst();
        assertEquals(Location.of(7, 7), firstClause.getLocation());

        assertEquals("first-clause-expression", firstClause.getExpression().getValue());
        assertEquals(Location.of(7, 13), firstClause.getExpression().getLocation());

        assertEquals("fist-destination", firstClause.getTo().getValue());
        assertEquals(Location.of(8, 11), firstClause.getTo().getLocation());

        Route secondClause = routerConfig.getRoutes().getValue().get(1);
        assertEquals(Location.of(9, 7), secondClause.getLocation());

        assertEquals("second-clause-expression", secondClause.getExpression().getValue());
        assertEquals(Location.of(9, 13), secondClause.getExpression().getLocation());

        assertEquals("second-destination", secondClause.getTo().getValue());
        assertEquals(Location.of(10, 11), secondClause.getTo().getLocation());


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
        assertEquals(Location.of(1, 1), traceAggregatorItem.getLocation());

        assertEquals("name-value", traceAggregatorItem.getName().getValue());
        assertEquals(Location.of(1, 19), traceAggregatorItem.getName().getLocation());

        assertEquals(1 , traceAggregatorItem.getTo().getValue().size());
        assertEquals("destination-value", traceAggregatorItem.getTo().getValue().getFirst().getValue());
        assertEquals(Location.of(2, 5), traceAggregatorItem.getTo().getValue().getFirst().getLocation());

        assertEquals("default", traceAggregatorItem.getType().getValue());
        assertEquals(Location.of(3, 7), traceAggregatorItem.getType().getLocation());

        var traceAggregatorConfig = assertInstanceOf(TraceAggregatorConfig.class, traceAggregatorItem.getConfig());
        assertEquals(Location.of(4, 1), traceAggregatorConfig.getLocation());

        assertEquals(1, traceAggregatorConfig.getTimeoutMillis().getValue());
        assertEquals(Location.of(5, 19), traceAggregatorConfig.getTimeoutMillis().getLocation());

        assertEquals(2, traceAggregatorConfig.getInactiveTimeoutMillis().getValue());
        assertEquals(Location.of(6, 28), traceAggregatorConfig.getInactiveTimeoutMillis().getLocation());

        assertEquals("expression value", traceAggregatorConfig.getCompletionExpression().getValue());
        assertEquals(Location.of(7, 26), traceAggregatorConfig.getCompletionExpression().getLocation());


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
        assertEquals(Location.of(1,1), pipelinesArray.getLocation());

        var pipelines = pipelinesArray.getValue();
        assertEquals(1, pipelines.size());
        TracePipelineItem pipeline = assertInstanceOf(TracePipelineItem.class, pipelines.getFirst());
        assertEquals(Location.of(1,3), pipeline.getLocation());

        assertEquals("name-value", pipeline.getName().getValue());
        assertEquals(Location.of(1,10), pipeline.getName().getLocation());

        assertEquals(1, pipeline.getFrom().size());
        assertEquals("origin-value", pipeline.getFrom().getFirst().getValue());
        assertEquals(Location.of(2,9), pipeline.getFrom().getFirst().getLocation());

        assertEquals(1, pipeline.getStart().size());
        assertEquals("spanlet-name", pipeline.getStart().getFirst().getValue());
        assertEquals(Location.of(3,10), pipeline.getStart().getFirst().getLocation());


        List<SpanletItem> spanlets = pipeline.getProcessors().getValue().stream().toList();
        assertEquals(1, spanlets.size());
        assertEquals(Location.of(4,3), pipeline.getProcessors().getLocation());

        SpanletItem spanletItem = spanlets.getFirst();
        assertEquals(Location.of(5,5), spanletItem.getLocation());

        assertEquals("spanlet-name", spanletItem.getName().getValue());
        assertEquals(Location.of(5,14), spanletItem.getName().getLocation());

        assertEquals(1, spanletItem.getTo().getValue().size());
        assertEquals(Location.of(6,9), spanletItem.getTo().getLocation());
        assertEquals("destination-value", spanletItem.getTo().getValue().getFirst().getValue());
        assertEquals(Location.of(6,9), spanletItem.getTo().getValue().getFirst().getLocation());

        assertEquals("processor", spanletItem.getType().getValue());
        assertEquals(Location.of(7,11), spanletItem.getType().getLocation());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, spanletItem.getConfig());
        assertEquals(Location.of(8,5), spanletItem.getConfig().getLocation());

        assertEquals("action-value", processorConfig.getAction().getValue());
        assertEquals(Location.of(9,15), processorConfig.getAction().getLocation());


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
        assertEquals(Location.of(1,1), globalConfig.getLocation());

        var receiverItemsArray = globalConfig.getReceivers();
        assertEquals(Location.of(1,1), receiverItemsArray.getLocation());

        List<ReceiverItem> receiverItems = receiverItemsArray.getValue();
        assertEquals(2, receiverItems.size());

        GrpcReceiverItem firstReceiver = assertInstanceOf(GrpcReceiverItem.class, receiverItems.getFirst());
        assertEquals(Location.of(2,3), firstReceiver.getLocation());

        assertEquals("first", firstReceiver.getName().getValue());
        assertEquals(Location.of(2,9), firstReceiver.getName().getLocation());

        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080),
                firstReceiver.getAddress().getValue());
        assertEquals(Location.of(3,12), firstReceiver.getAddress().getLocation());

        GrpcReceiverItem secondReceiver = assertInstanceOf(GrpcReceiverItem.class, receiverItems.get(1));
        assertEquals(Location.of(4,3), secondReceiver.getLocation());

        assertEquals("second", secondReceiver.getName().getValue());
        assertEquals(Location.of(4,9), secondReceiver.getName().getLocation());

        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081),
                secondReceiver.getAddress().getValue());
        assertEquals(Location.of(5,12), secondReceiver.getAddress().getLocation());

        var exporterItemsArray = globalConfig.getExporters();
        assertEquals(Location.of(6,1), exporterItemsArray.getLocation());

        List<ExporterItem> exporterItems = exporterItemsArray.getValue();
        assertEquals(2, exporterItems.size());

        GrpcExporterItem firstExporter = assertInstanceOf(GrpcExporterItem.class, exporterItems.getFirst());
        assertEquals(Location.of(7,3), firstExporter.getLocation());

        assertEquals("first", firstExporter.getName().getValue());
        assertEquals(Location.of(7,9), firstExporter.getName().getLocation());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080),
                firstExporter.getAddress().getValue());
        assertEquals(Location.of(8,12), firstExporter.getAddress().getLocation());

        GrpcExporterItem secondExporter = assertInstanceOf(GrpcExporterItem.class, exporterItems.get(1));
        assertEquals("second", secondExporter.getName().getValue());
        assertEquals(Location.of(9,9), secondExporter.getName().getLocation());
        assertEquals(InetSocketAddress.createUnresolved("localhost", 8081),
                secondExporter.getAddress().getValue());
        assertEquals(Location.of(10,12), secondExporter.getAddress().getLocation());

        var pipelinesArray = globalConfig.getPipelines();
        assertEquals(Location.of(11,1), pipelinesArray.getLocation());

        var pipelines = pipelinesArray.getValue();
        assertEquals(1, pipelines.size());

        var pipeline = pipelines.getFirst();
        assertEquals(Location.of(12,3), pipeline.getLocation());

        assertEquals("pipeline name", pipeline.getName().getValue());
        assertEquals(Location.of(12,10), pipeline.getName().getLocation());

        assertEquals(1, pipeline.getFrom().size());
        assertEquals("first", pipeline.getFrom().getFirst().getValue());
        assertEquals(Location.of(13,9), pipeline.getFrom().getFirst().getLocation());

        assertEquals(1, pipeline.getStart().size());
        assertEquals("spanlet-name", pipeline.getStart().getFirst().getValue());
        assertEquals(Location.of(14,10), pipeline.getStart().getFirst().getLocation());

        assertEquals(Location.of(15,3), pipeline.getProcessors().getLocation());
        List<SpanletItem> spanletItems = pipeline.getProcessors().getValue().stream().toList();

        assertEquals(1, spanletItems.size());

        SpanletItem spanletItem = spanletItems.getFirst();
        assertEquals(Location.of(16,5), spanletItem.getLocation());

        assertEquals("spanlet-name", spanletItem.getName().getValue());
        assertEquals(Location.of(16,14), spanletItem.getName().getLocation());

        assertEquals(1, spanletItem.getTo().getValue().size());
        assertEquals("destination-value", spanletItem.getTo().getValue().getFirst().getValue());
        assertEquals(Location.of(17,9), spanletItem.getTo().getValue().getFirst().getLocation());


        assertEquals("processor", spanletItem.getType().getValue());
        assertEquals(Location.of(18,11), spanletItem.getType().getLocation());

        var processorConfig = assertInstanceOf(ProcessorConfig.class, spanletItem.getConfig());
        assertEquals(Location.of(19,5), processorConfig.getLocation());

        assertEquals("action-value", processorConfig.getAction().getValue());
        assertEquals(Location.of(20,15), processorConfig.getAction().getLocation());


    }
}