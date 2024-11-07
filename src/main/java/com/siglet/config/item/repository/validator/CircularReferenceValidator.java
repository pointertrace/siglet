package com.siglet.config.item.repository.validator;

import com.siglet.SigletError;
import com.siglet.config.item.repository.Node;
import com.siglet.config.item.repository.NodeRepository;
import com.siglet.config.item.repository.ReceiverNode;
import com.siglet.config.item.repository.SpanletNode;

import java.util.ArrayList;
import java.util.List;

public class CircularReferenceValidator implements RepositoryValidator {

    @Override
    public void validate(NodeRepository nodeRepository) {
        List<ReceiverNode> receivers = nodeRepository.getNodes().stream()
                .filter(ReceiverNode.class::isInstance)
                .map(ReceiverNode.class::cast)
                .toList();

        List<String> path = new ArrayList<>();
        for (ReceiverNode receiver : receivers) {
//            path.add(receiver.getName());
            navigate(path, receiver);
        }

    }

    public void navigate(List<String> path, Node<?> current) {
        if (current instanceof SpanletNode spanletNode) {
            for (Node<?> next : spanletNode.getTo()) {
                if (path.contains(next.getName())) {
                    throw new SigletError("Circular reference: " + String.join("->", path));
                }
                List<String> newPath = new ArrayList<>(path);
//                newPath.add(next.getName());
                navigate(newPath, next);
            }
        }

    }
}
