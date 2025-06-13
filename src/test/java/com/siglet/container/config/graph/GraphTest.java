package com.siglet.container.config.graph;

import com.siglet.SigletError;
import com.siglet.container.config.raw.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private Graph graph;

    private GrpcReceiverConfig receiverItem;

    private PipelineConfig pipelineConfig;

    private ProcessorConfig sigletConfig;

    private GrpcExporterConfig exporterItem;

    @BeforeEach
    public void setUp() {
        graph = new Graph();

        receiverItem = new GrpcReceiverConfig();
        receiverItem.setName("receiver");

        pipelineConfig = new PipelineConfig();
        pipelineConfig.setName("pipeline");
        pipelineConfig.setFrom("receiver");

        pipelineConfig.setStart(List.of(new LocatedString("sigletClass", null)));


        sigletConfig = new ProcessorConfig();
        sigletConfig.setName("sigletClass");
        sigletConfig.setTo(List.of(new LocatedString("exporter", null)));
        sigletConfig.setPipeline("pipeline");

        exporterItem = new GrpcExporterConfig();
        exporterItem.setName("exporter");


        graph.addItem(receiverItem);
        graph.addItem(pipelineConfig);
        graph.addItem(sigletConfig);
        graph.addItem(exporterItem);
    }


    @Test
    void getNodeByName() {

        BaseNode receiverNode = graph.getNodeByName("receiver");

        assertSame(receiverItem, receiverNode.getConfig());

    }

    @Test
    void getNodeByName_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> graph.getNodeByName("non-existent name"));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodeByNameAndType() {

        BaseNode receiverNode = graph.getNodeByNameAndType("receiver", ReceiverNode.class);

        assertSame(receiverItem, receiverNode.getConfig());

    }

    @Test
    void getNodeByNameAndType_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> graph.getNodeByNameAndType("non-existent " +
                "name", ReceiverNode.class));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodeByNameAndType_differentType() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> graph.getNodeByNameAndType("receiver",
                ProcessorNode.class));

        assertEquals("Node named [receiver] is ReceiverNode and should be ProcessorNode", e.getMessage());
    }

    @Test
    void getNodesByName() {

        List<BaseNode> nodes = graph.getNodesByName(List.of("receiver", "pipeline"));

        assertNotNull(nodes);
        assertEquals(2, nodes.size());
        assertEquals(receiverItem, nodes.getFirst().getConfig());
        assertEquals(pipelineConfig, nodes.get(1).getConfig());

    }

    @Test
    void getNodesByName_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () ->
                graph.getNodesByName(List.of("receiver", "non-existent name")));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodesByNameAndType() {

        List<ReceiverNode> receiverNodes = graph.getNodesByNameAndType(List.of("receiver"),
                ReceiverNode.class);

        assertNotNull(receiverNodes);
        assertEquals(1, receiverNodes.size());
        assertEquals(receiverItem, receiverNodes.getFirst().getConfig());

    }

    @Test
    void getNodesByNameAndType_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> graph.getNodesByNameAndType(
                List.of("non-existent name"), ReceiverNode.class));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodesByNameAndType_differentType() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> graph.getNodesByNameAndType(
                List.of("receiver","sigletClass"), ProcessorNode.class));

        assertEquals("Node named [receiver] is ReceiverNode and should be ProcessorNode", e.getMessage());
    }

    @Test
    void connect() {

        graph.connect();

        ReceiverNode receiverNode = graph.getNodeByNameAndType("receiver",ReceiverNode.class);
        PipelineNode pipelineNode = graph.getNodeByNameAndType("pipeline", PipelineNode.class);
        ProcessorNode processorNode = graph.getNodeByNameAndType("sigletClass", ProcessorNode.class);
        ExporterNode exporterNode = graph.getNodeByNameAndType("exporter", ExporterNode.class);

        assertEquals(1,receiverNode.getTo().size());
        assertEquals(pipelineNode, receiverNode.getTo().getFirst());

        assertEquals(1 , pipelineNode.getFrom().size());
        assertEquals(receiverNode, pipelineNode.getFrom().getFirst());
        assertEquals(1, pipelineNode.getStart().size());
        assertEquals(processorNode, pipelineNode.getStart().getFirst());

        assertEquals(pipelineNode, processorNode.getPipeline());
        assertEquals(1, processorNode.getTo().size());
        assertEquals(exporterNode, processorNode.getTo().getFirst());

        assertEquals(1,exporterNode.getFrom().size());
        assertEquals(processorNode, exporterNode.getFrom().getFirst());

    }
}