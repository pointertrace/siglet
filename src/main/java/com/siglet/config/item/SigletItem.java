package com.siglet.config.item;

import com.siglet.config.located.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SigletItem extends Item {

    private SigletKind kind;

    private Location kindLocation;

    private final List<LocatedString> to = new ArrayList<>();

    private Location toLocation;

    private String type;

    private Location typeLocation;

    private Object config;

    private Location configLocation;

    private String pipeline;

    public SigletKind getKind() {
        return kind;
    }

    public void setKind(SigletKind kind) {
        this.kind = kind;
    }

    public Location getKindLocation() {
        return kindLocation;
    }

    public void setKindLocation(Location kindLocation) {
        this.kindLocation = kindLocation;
    }

    public List<LocatedString> getTo() {
        return Collections.unmodifiableList(to);
    }

    // TODO test
    public List<String> getToNames() {
        return to.stream().map(LocatedString::getValue).toList();
    }

    public void setTo(List<LocatedString> to) {
        this.to.addAll(to);
    }

    public void setToSingleValue(LocatedString to) {
        this.to.add(to);
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Location getTypeLocation() {
        return typeLocation;
    }

    public void setTypeLocation(Location typeLocation) {
        this.typeLocation = typeLocation;
    }

    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public Location getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(Location configLocation) {
        this.configLocation = configLocation;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  siglet:\n");


        sb.append(super.describe(level + 1));

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(kindLocation.describe());
        sb.append("  kind: ");
        sb.append(getKind());
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(toLocation.describe());
        sb.append("  to:\n");
        for (LocatedString toName : getTo()) {
            sb.append(getDescriptionPrefix(level + 2));
            sb.append(toName.getLocation().describe());
            sb.append("  ");
            sb.append(toName.getValue());
            sb.append("\n");
        }

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getTypeLocation().describe());
        sb.append("  type: ");
        sb.append(getType());

        sb.append("\n");
        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getConfigLocation().describe());
        if (getConfig() == null) {
            sb.append("  config: null");
        } else {
            if (getConfig() instanceof Describable describable) {
                sb.append(" config:\n");
                sb.append(describable.describe(level + 2));
            } else {
                sb.append(" config: [cannot generate description]\n");
            }
        }
        return sb.toString();
    }
}
