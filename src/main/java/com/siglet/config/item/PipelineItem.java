package com.siglet.config.item;

import com.siglet.config.located.Location;

import java.util.List;

public class PipelineItem extends Item {

    private Signal signal;

    private Location signalLocation;

    private String from;

    private Location fromLocation;

    private List<LocatedString> start;

    private Location startLocation;

    private List<SigletItem> siglets;

    private Location sigletsLocation;


    public Signal getSignal() {
        return signal;
    }

    public void setSignal(Signal signal) {
        this.signal = signal;
    }

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

    public List<SigletItem> getSiglets() {
        return siglets;
    }

    public void setSiglets(List<SigletItem> siglets) {
        this.siglets = siglets;
    }

    @Override
    public void afterSetValues() {
        siglets.forEach(p -> p.setPipeline(getName()));
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  Pipeline:");
        sb.append("\n");

        sb.append(super.describe(level+1));

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(signalLocation.describe());
        sb.append("  signal: ");
        sb.append(signal);
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(fromLocation.describe());
        sb.append("  from: ");
        sb.append(from);
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(startLocation.describe());
        sb.append("  start:\n");

        for(LocatedString startLocated: start) {
            sb.append(getDescriptionPrefix(level + 2));
            sb.append(startLocated.getLocation().describe());
            sb.append("  ");
            sb.append(startLocated.getValue());
            sb.append("\n");
        }

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(sigletsLocation.describe());
        sb.append("  siglets:");
        sb.append("\n");
        for (SigletItem sigletItem : siglets) {
            sb.append(sigletItem.describe(level + 2));
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

