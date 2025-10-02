package io.github.pointertrace.siglet.impl.config.raw;


import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.located.Location;

import java.util.ArrayList;
import java.util.List;

public class PipelineConfig extends BaseConfig {

    private Location signalLocation;

    private String from;

    private Location fromLocation;

    private List<LocatedString> start;

    private Location startLocation;

    private List<ProcessorConfig> processors = new ArrayList<>();

    private Location sigletsLocation;

    public Location getSignalLocation() {
        return signalLocation;
    }

    public void setSignalLocation(Location signalLocation) {
        this.signalLocation = signalLocation;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public List<LocatedString> getStart() {
        return start;
    }

    // todo testar
    public List<String> getStartNames() {
        return start.stream().map(LocatedString::getValue).toList();
    }
    public void setStart(List<LocatedString> start) {
        this.start = start;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public void setStartSingleValue(LocatedString start) {
        this.start = List.of(start);
    }

    public List<ProcessorConfig> getProcessors() {
        return processors;
    }

    public void setProcessors(List<ProcessorConfig> processors) {
        this.processors = processors;
    }

    @Override
    public void afterSetValues() {
        processors.forEach(p -> p.setPipeline(getName()));
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getLocation().describe());
        sb.append("  PipelineConfig:");
        sb.append("\n");

        sb.append(super.describe(level+1));

        sb.append(Describable.prefix(level + 1));
        sb.append(fromLocation.describe());
        sb.append("  from: ");
        sb.append(from);
        sb.append("\n");

        sb.append(Describable.prefix(level + 1));
        sb.append(startLocation.describe());
        sb.append("  start:\n");

        for(LocatedString startLocated: start) {
            sb.append(Describable.prefix(level + 2));
            sb.append(startLocated.getLocation().describe());
            sb.append("  ");
            sb.append(startLocated.getValue());
            sb.append("\n");
        }

        sb.append(Describable.prefix(level + 1));
        sb.append(sigletsLocation.describe());
        sb.append("  processors:\n");
        for (ProcessorConfig processorConfig : processors) {
            sb.append(processorConfig.describe(level + 2));
        }

        return sb.toString();
    }

    public Location getSigletsLocation() {
        return sigletsLocation;
    }

    public void setSigletsLocation(Location sigletsLocation) {
        this.sigletsLocation = sigletsLocation;
    }

}

