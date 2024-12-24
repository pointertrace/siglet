package com.siglet.config.item;

public class MetricletItem extends ProcessorItem {

    private ValueItem<String> type;

    public ValueItem<String> getType() {
        return type;
    }

    public void setType(ValueItem<String> type) {
        this.type = type;
    }
    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  metricletItem");

        sb.append("\n");
        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getName().getLocation().describe());
        sb.append("  name");
        sb.append("\n");
        sb.append(getName().describe(level + 2));

        sb.append("\n");
        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getTo().getLocation().describe());
        sb.append("  to");
        sb.append("\n");
        sb.append(getTo().describe(level + 2));

        sb.append("\n");
        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getType().getLocation().describe());
        sb.append("  type");
        sb.append("\n");
        sb.append(getType().describe(level + 2));

        sb.append("\n");
        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getConfig().getLocation().describe());
        sb.append("  config");
        sb.append("\n");
        sb.append(getConfig().describe(level + 2));

        return sb.toString();
    }
}
