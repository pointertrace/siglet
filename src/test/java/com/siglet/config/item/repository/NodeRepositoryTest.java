package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeRepositoryTest {

    private NodeRepository nodeRepository;

    private GrpcReceiverItem receiverItem;

    private PipelineItem pipelineItem;

    private SigletItem sigletItem;

    private GrpcExporterItem exporterItem;

    @BeforeEach
    public void setUp() {
        nodeRepository = new NodeRepository();

        receiverItem = new GrpcReceiverItem();
        receiverItem.setName("receiver");

        pipelineItem = new PipelineItem();
        pipelineItem.setName("pipeline");
        pipelineItem.setFrom("receiver");

        pipelineItem.setStart(List.of(new LocatedString("siglet", null)));


        sigletItem = new SigletItem();
        sigletItem.setName("siglet");
        sigletItem.setTo(List.of(new LocatedString("exporter", null)));
        sigletItem.setPipeline("pipeline");

        exporterItem = new GrpcExporterItem();
        exporterItem.setName("exporter");


        nodeRepository.addItem(receiverItem);
        nodeRepository.addItem(pipelineItem);
        nodeRepository.addItem(sigletItem);
        nodeRepository.addItem(exporterItem);
    }


    @Test
    void getNodeByName() {

        Node<?> receiverNode = nodeRepository.getNodeByName("receiver");

        assertSame(receiverItem, receiverNode.getItem());

    }

    @Test
    void getNodeByName_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> nodeRepository.getNodeByName("non-existent name"));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodeByNameAndType() {

        Node<?> receiverNode = nodeRepository.getNodeByNameAndType("receiver", ReceiverNode.class);

        assertSame(receiverItem, receiverNode.getItem());

    }

    @Test
    void getNodeByNameAndType_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> nodeRepository.getNodeByNameAndType("non-existent " +
                "name", ReceiverNode.class));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodeByNameAndType_differentType() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> nodeRepository.getNodeByNameAndType("receiver",
                SigletNode.class));

        assertEquals("Node named [receiver] is ReceiverNode and should be SigletNode", e.getMessage());
    }

    @Test
    void getNodesByName() {

        List<Node<?>> nodes = nodeRepository.getNodesByName(List.of("receiver", "pipeline"));

        assertNotNull(nodes);
        assertEquals(2, nodes.size());
        assertEquals(receiverItem, nodes.getFirst().getItem());
        assertEquals(pipelineItem, nodes.get(1).getItem());

    }

    @Test
    void getNodesByName_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () ->
                nodeRepository.getNodesByName(List.of("receiver", "non-existent name")));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodesByNameAndType() {

        List<ReceiverNode> receiverNodes = nodeRepository.getNodesByNameAndType(List.of("receiver"),
                ReceiverNode.class);

        assertNotNull(receiverNodes);
        assertEquals(1, receiverNodes.size());
        assertEquals(receiverItem, receiverNodes.getFirst().getItem());

    }

    @Test
    void getNodesByNameAndType_notFound() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> nodeRepository.getNodesByNameAndType(
                List.of("non-existent name"), ReceiverNode.class));

        assertEquals("Could not find any node named [non-existent name]", e.getMessage());
    }

    @Test
    void getNodesByNameAndType_differentType() {

        SigletError e = assertThrowsExactly(SigletError.class, () -> nodeRepository.getNodesByNameAndType(
                List.of("receiver","siglet"), SigletNode.class));

        assertEquals("Node named [receiver] is ReceiverNode and should be SigletNode", e.getMessage());
    }

    @Test
    void connect() {

        nodeRepository.connect();

        ReceiverNode receiverNode = nodeRepository.getNodeByNameAndType("receiver",ReceiverNode.class);
        PipelineNode pipelineNode = nodeRepository.getNodeByNameAndType("pipeline", PipelineNode.class);
        SigletNode sigletNode = nodeRepository.getNodeByNameAndType("siglet", SigletNode.class);
        ExporterNode exporterNode = nodeRepository.getNodeByNameAndType("exporter", ExporterNode.class);

        assertEquals(1,receiverNode.getTo().size());
        assertEquals(pipelineNode, receiverNode.getTo().getFirst());

        assertEquals(1 , pipelineNode.getFrom().size());
        assertEquals(receiverNode, pipelineNode.getFrom().getFirst());
        assertEquals(1, pipelineNode.getStart().size());
        assertEquals(sigletNode, pipelineNode.getStart().getFirst());

        assertEquals(pipelineNode,sigletNode.getPipeline());
        assertEquals(1, sigletNode.getTo().size());
        assertEquals(exporterNode, sigletNode.getTo().getFirst());

        assertEquals(1,exporterNode.getFrom().size());
        assertEquals(sigletNode, exporterNode.getFrom().getFirst());

    }
}