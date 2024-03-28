package com.siglet.config.builder;

import com.siglet.SigletError;
import com.siglet.utils.Joining;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionValidator implements Validator {


    @Override
    public void validate(GlobalConfigBuilder globalConfigBuilder) throws SigletError {
        NodeRepository nodeRepository = new NodeRepository();

        globalConfigBuilder.getReceivers().forEach(receiver ->
                nodeRepository.addNode(new Node(receiver.getName(), NodeType.RECEIVER)));

        globalConfigBuilder.getExporters().forEach(exporter ->
                nodeRepository.addNode(new Node(exporter.getName(),  NodeType.EXPORTER)));

        globalConfigBuilder.getPipelines().forEach(pipeline ->
                nodeRepository.addNode(new Node(pipeline.getName(),  NodeType.PIPELINE)));



//        globalConfigBuilder.getPipelines().forEach(pipeline -> {
//            Node startNode = nodeRepository.getByName(pipeline.getStart());
//            Node toNode= nodeRepository.getByName(pipeline.getStart())
//
//
//        });





    }

    private static class NodeRepository {
        Map<String, Node> nodes = new HashMap<>();

        public void addNode(Node node) {
            nodes.put(node.getName(), node);
        }

        public List<Node> getByType(NodeType nodeType) {
            return nodes.values().stream().filter(node -> node.getType() == nodeType).toList();
        }

        public Node getByName(String name) {
            return nodes.get(name);
        }

        public Collection<Node> getAll() {
            return nodes.values();
        }


    }

    private static enum NodeType {
        RECEIVER, EXPORTER, PIPELINE, SPANLET
    }

    private static class Node {

        private final String name;

        private final NodeType type;

        private Node from;

        private Node to;

        private Node(String name, NodeType type) {
            this.name = name;
            this.type = type;
        }

        public NodeType getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public Node getTo() {
            return to;
        }

        public void setTo(Node to) {
            this.to = to;
        }

        public Node getFrom() {
            return from;
        }

        public void setFrom(Node from) {
            this.from = from;
        }
    }


}
