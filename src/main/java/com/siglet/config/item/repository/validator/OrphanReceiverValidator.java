package com.siglet.config.item.repository.validator;

import com.siglet.config.item.repository.NodeRepository;

public class OrphanReceiverValidator implements RepositoryValidator {

    @Override
    public void validate(NodeRepository nodeRepository) {
//        Set<String> orphanReceivers = nodeRepository.getNodes().stream()
//                .filter(ReceiverNode.class::isInstance)
//                .map(ReceiverNode.class::cast)
//                .filter(receiverNode -> receiverNode.getTo().isEmpty())
//                .map(Node::getName)
//                .collect(Collectors.toSet());
//
//        if (orphanReceivers.size() == 1) {
//            throw new SigletError("Receiver [" + new ArrayList<>(orphanReceivers).getFirst() + "] is being used!");
//        } else if (orphanReceivers.size() > 1) {
//            throw new SigletError("Receivers [" + String.join(",", orphanReceivers) + "] are not being used!");
//        }
    }
}
