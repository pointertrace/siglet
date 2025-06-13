package com.siglet.container.config.raw;


public class DebugExporterConfig extends ExporterConfig {

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder();
        sb.append(getLocation().describe());
        sb.append("  DebugExporterConfig");
        sb.append("\n");

        sb.append(super.describe(level+1));

        return sb.toString();
    }

}
