package com.siglet.config.item;

import com.siglet.config.located.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SigletItem extends Item {

    private ProcessorKind kind;

    private Location kindLocation;

    private String type;

    private Location typeLocation;

    private Object config;

    private Location configLocation;

    private String pipeline;

    private final List<LocatedString> to = new ArrayList<>();

    private Location toLocation;

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public List<LocatedString> getTo() {
        return Collections.unmodifiableList(to);
    }

    public void setTo(List<LocatedString> to) {
        this.to.addAll(to);
    }

    public void setToSingleValue(LocatedString to) {
        this.to.add(to);
    }

    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  kind: ");
        sb.append(getKind());
        sb.append("\n");

        sb.append(super.describe(level+1));

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(toLocation.describe());
        sb.append("  to:\n");
        for(LocatedString to: getTo()) {
            sb.append(getDescriptionPrefix(level + 2));
            sb.append(to.getLocation().describe());
            sb.append("  ");
            sb.append(to.getValue());
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
                sb.append(describable.describe(level +2));
            } else {
                sb.append(" config: [cannot generate description]\n");
            }
        }
        return sb.toString();
    }


    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public Location getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(Location configLocation) {
        this.configLocation = configLocation;
    }

    public ProcessorKind getKind() {
        return kind;
    }

    public void setKind(ProcessorKind kind) {
        this.kind = kind;
    }

    public Location getKindLocation() {
        return kindLocation;
    }

    public void setKindLocation(Location kindLocation) {
        this.kindLocation = kindLocation;
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
}
