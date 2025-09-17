package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.located.Location;

public class ReceiverConfig extends BaseConfig {

    private String type;

    private Location typeLocation;

    private Object config;

    private Location configLocation;

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
        if (config instanceof ReceiverConfigSetter receiverConfigSetter) {
            receiverConfigSetter.setReceiverConfig(this);
        }
    }

    public Location getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(Location configLocation) {
        this.configLocation = configLocation;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getLocation().describe());
        sb.append("  receiverConfig:\n");
        sb.append(super.describe(level + 1));

        sb.append(Describable.prefix(level + 1));
        sb.append(getTypeLocation().describe());
        sb.append("  type: ");
        sb.append(getType());
        if (getConfig() != null) {
            sb.append("\n");
            sb.append(Describable.prefix(level + 1));
            sb.append(getConfigLocation().describe());
            sb.append("  config: (");
            if (getConfig().getClass().getName().startsWith("io.github.pointertrace.siglet")) {
                sb.append(getConfig().getClass().getSimpleName());
            } else {
                sb.append(getConfig().getClass().getName());
            }
            sb.append(")\n");
            if (getConfig() instanceof Describable describableConfig) {
                sb.append(describableConfig.describe(level + 2));
            } else {
                sb.append(getConfig().toString());
            }
        }
        return sb.toString();
    }
}
