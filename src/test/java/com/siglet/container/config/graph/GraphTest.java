package com.siglet.container.config.graph;

import com.siglet.SigletError;
import com.siglet.container.Siglet;
import com.siglet.container.config.raw.*;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.ContextFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private Context context;

    private ContextFactory contextFactory;

    private Graph graph;


    @BeforeEach
    void setUp() {
        String config = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signalType.name = signalType.name +"-suffix"
                """;


        contextFactory = new ContextFactory();
        context = contextFactory.create(config, List.of());

        graph = context.getGraph();
    }


    @Test
    void getNodeByName() {

        BaseNode node = graph.getNodeByName("receiver");
        assertNotNull(node);


        ReceiverNode receiverNode = assertInstanceOf(ReceiverNode.class, node);

        assertEquals("receiver", receiverNode.getConfig().getName());

    }

    @Test
    void getNodeByName_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> graph.getNodeByName("non-existent name"));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodeByNameAndType() {

        ReceiverNode receiverNode = graph.getNodeByNameAndType("receiver", ReceiverNode.class);
        assertNotNull(receiverNode);

        assertEquals("receiver", receiverNode.getConfig().getName());
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
        ReceiverNode receiverNode = assertInstanceOf(ReceiverNode.class, nodes.getFirst());
        assertEquals("receiver", receiverNode.getConfig().getName());

        PipelineNode pipelineNode = assertInstanceOf(PipelineNode.class, nodes.get(1));
        assertEquals("pipeline", pipelineNode.getConfig().getName());

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
        ReceiverNode receiverNode = receiverNodes.getFirst();
        assertEquals("receiver", receiverNode.getConfig().getName());

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
                List.of("receiver","spanlet"), ProcessorNode.class));

        assertEquals("Node named [receiver] is ReceiverNode and should be ProcessorNode", e.getMessage());
    }

    @Test
    void connect() {

        ReceiverNode receiverNode = graph.getNodeByNameAndType("receiver",ReceiverNode.class);
        PipelineNode pipelineNode = graph.getNodeByNameAndType("pipeline", PipelineNode.class);
        ProcessorNode processorNode = graph.getNodeByNameAndType("spanlet", ProcessorNode.class);
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