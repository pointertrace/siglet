package com.siglet.config.item;

import java.util.ArrayList;
import java.util.List;

public class PipelineItem<T extends ProcessorItem> extends Item {

    private ArrayItem<T> processors;

    private List<ValueItem<String>> from = new ArrayList<>();

    private List<ValueItem<String>> start = new ArrayList<>();

    public ArrayItem<T> getProcessors() {
        return processors;
    }

    public void setProcessors(ArrayItem<T> processors) {
        this.processors = processors;
    }

    public List<ValueItem<String>> getFrom() {
        return from;
    }

    public void setFrom(List<ValueItem<String>> from) {
        this.from = from;
    }

    public void setFromSingleValue(ValueItem<String> from) {
        this.from = List.of(from);
    }

    public List<ValueItem<String>> getStart() {
        return start;
    }

    public void setStart(List<ValueItem<String>> start) {
        this.start = start;
    }

    public void setStartSingleValue(ValueItem<String> start) {
        this.start = List.of(start);
    }

    @Override
    public void afterSetValues() {
        processors.getValue().forEach(p -> p.setPipeline(new ValueItem<>(getLocation(), getName().getValue())));
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  PipelineItem");
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getName().getLocation().describe());
        sb.append("  name");
        sb.append("\n");
        sb.append(getName().describe(level + 2));
        sb.append("\n");

        for (ValueItem<String> fromName : from) {
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(fromName.getLocation().describe());
            sb.append("  from");
            sb.append("\n");
            sb.append(fromName.describe(level + 2));
            sb.append("\n");
        }

        for (ValueItem<String> startName : start) {
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(startName.getLocation().describe());
            sb.append("  start");
            sb.append("\n");
            sb.append(startName.describe(level + 2));
            sb.append("\n");
        }

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(processors.getLocation().describe());
        sb.append("  processors");
        sb.append("\n");
        sb.append(processors.describe(level + 2));

        return sb.toString();
    }

}

