package com.siglet.utils;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.*;

import java.io.StringReader;

public class Main {


    public static void main(String[] args) throws Exception {
//        String document = "\nexample:\n  key: value\n";
        String document = """
                parent-object:
                  child-object:
                    property: value
                    array:
                    - first:
                       element-1: 1000000000000000000000
                       element-2: value-2
                    - second element""";
        System.out.println(document);
        System.out.println("----------------------------");
        Yaml yaml = new Yaml();
        Node node = yaml.compose(new StringReader(document));
//        System.out.println(Integer.parseInt("1000000000000"));
//        ScalarNode n;
//        MappingNode n1;
//        SequenceNode n2;
//
//        NodeTuple n3;
//        n3.getKeyNode().getEndMark().
        printNodeMarkers(node);
    }

    private static void printNodeMarkers(Node node) {
        Mark startMark = node.getStartMark();
        Mark endMark = node.getEndMark();

        System.out.println("nodeid:"+ node.getNodeId());
        switch (node){
            case ScalarNode scalarNode:
                System.out.println("value:" + scalarNode.getValue());
                System.out.println("type:" + scalarNode.getTag());
                System.out.println(scalarNode.getValue());
                break;
            case MappingNode mappingNode:
                System.out.println("object ");
                System.out.println("type:" + mappingNode.getTag());
                break;
            case SequenceNode sequenceNode:
                System.out.println("array ");
                System.out.println("type:" + sequenceNode.getTag());

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + node);
        }
        System.out.println("nodevalue:"+ node);
        System.out.println(String.format("Node [%s] starts at line %s, column %s",
                node.getNodeId().name(), startMark.getLine(), startMark.getColumn()));
        System.out.println(String.format("Node [%s] ends at line %s, column %s",
                node.getNodeId().name(), endMark.getLine(), endMark.getColumn()));

        if (node instanceof MappingNode) {
            for (NodeTuple tuple : ((MappingNode) node).getValue()) {
                printNodeMarkers(tuple.getKeyNode());
                printNodeMarkers(tuple.getValueNode());
            }
        } else if (node instanceof SequenceNode) {
            for (Node subnode : ((SequenceNode) node).getValue()) {
                printNodeMarkers(subnode);
            }
        }
    }
}
