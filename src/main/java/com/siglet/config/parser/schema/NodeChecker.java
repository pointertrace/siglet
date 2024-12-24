package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueTransformer;

import java.util.Collections;
import java.util.List;

public abstract class NodeChecker {

    private static final String PREFIX = "  ";

    public abstract void check(Node node) throws SchemaValidationError;

    public abstract String getName();

    public ValueTransformer getValueTransformer() {
        return null;
    }

    public List<? extends NodeChecker> getChildren() {
        return Collections.emptyList();
    }

    public String getDescription() {
        return "";
    }

    public String describe() {
        return describe(0);
    }

    protected String describe(int level) {
        StringBuilder sb = new StringBuilder(PREFIX.repeat(level));
        sb.append(getName());
        if (!"".equals(getDescription())) {
            sb.append("  (");
            sb.append(getDescription());
            sb.append(")");
        }
        for (NodeChecker child : getChildren()) {
            sb.append('\n');
            sb.append(child.describe(level + 1));
        }
        return sb.toString();
    }

}
