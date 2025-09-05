package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.Utils;
import io.github.pointertrace.siglet.parser.ValueTransformer;

import java.util.Collections;
import java.util.List;

public abstract class BaseNodeChecker implements NodeChecker {

    private static final String PREFIX = "  ";

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

    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Utils.repeat(PREFIX,level));
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
