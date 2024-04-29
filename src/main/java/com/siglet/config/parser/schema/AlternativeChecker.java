package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;

import java.util.ArrayList;
import java.util.List;

public class AlternativeChecker implements NodeChecker {

    private final List<NodeChecker> alternatives;

    public AlternativeChecker(NodeChecker... alternatives) {
        this.alternatives = List.of(alternatives);
    }


    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        List<SingleSchemaValidationError> alternativeErrors = new ArrayList<>();
        List<String> failedCheckers = new ArrayList<>();
        for (NodeChecker alternative : alternatives) {
            try {
                alternative.check(node);
                return;
            } catch (SingleSchemaValidationError e) {
                alternativeErrors.add(e);
                failedCheckers.add(alternative.getName());
            }
        }
        StringBuilder tmp = new StringBuilder("None of alternatives are valid:\n");
        for (int i = 0; i < failedCheckers.size(); i++) {
            tmp.append("  - ")
                    .append(failedCheckers.get(i))
                    .append(" because: ")
                    .append(alternativeErrors.get(i).toString())
                    .append("\n");
        }

        throw new MultipleSchemaValidationError(tmp.toString(), alternativeErrors);
    }

    @Override
    public String getName() {
        return "alternative";
    }

}
