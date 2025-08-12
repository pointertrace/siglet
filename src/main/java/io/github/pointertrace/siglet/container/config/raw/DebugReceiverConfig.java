package io.github.pointertrace.siglet.container.config.raw;


import io.github.pointertrace.siget.parser.Describable;

public class DebugReceiverConfig extends ReceiverConfig {


    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getLocation().describe());
        sb.append("  DebugReceiverConfig");
        sb.append("\n");

        sb.append(super.describe(level+1));
        return sb.toString();

    }

}
