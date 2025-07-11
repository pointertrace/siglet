package com.siglet.container.config.graph.validator;

import com.siglet.container.config.graph.BaseNode;
import com.siglet.container.config.graph.Graph;
import com.siglet.container.config.graph.ReceiverNode;

import java.util.ArrayList;
import java.util.List;

public class CircularReferenceValidator implements GraphValidator {

    @Override
    public void validate(Graph graph) {
        List<ReceiverNode> receivers = graph.getNodes().stream()
                .filter(ReceiverNode.class::isInstance)
                .map(ReceiverNode.class::cast)
                .toList();

        List<String> path = new ArrayList<>();
        for (ReceiverNode receiver : receivers) {
//            path.add(receiver.getName());
            navigate(path, receiver);
        }

    }

    public void navigate(List<String> path, BaseNode current) {
        if (current instanceof Object) {
//            for (Node<?> next : spanletNode.getTo()) {
//                if (path.contains(next.getName())) {
//                    throw new SigletError("Circular reference: " + String.join("->", path));
//                }
//                List<String> newPath = new ArrayList<>(path);
//                newPath.add(next.getName());
//                navigate(newPath, next);
//            }
        }

    }
}
